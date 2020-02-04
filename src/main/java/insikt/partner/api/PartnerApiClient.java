package insikt.partner.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartnerApiClient {

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    @Autowired
    @Qualifier("partnerApiRestTemplate")
    private RestTemplate partnerApiRestTemplate;

    @Value("${insikt.partner.api.url}")
    private String url;

    public Map echo(final String message) {
        Map<String, Object> apiParams = new HashMap();
        apiParams.put("message", message);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "/echo");
        for (String s : apiParams.keySet()) {
            builder.queryParam(s, apiParams.get(s));
        }

        log.trace("Sending: echo {}", builder.build(false).toUri());

        ResponseEntity<Map> responseEntity = partnerApiRestTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, Map.class);

        log.trace("Returned: response {} ", responseEntity.getBody());

        return responseEntity.getBody();
    }

    public Optional<PartnerApiAuthorizationResponse> authorize(String partnerLocationId,
                                                               String providerTransactionId,
                                                               String partnerTransactionId,
                                                               Double totalPaymentAmount,
                                                               String paymentMethod,
                                                               Integer paymentMeanId,
                                                               String productAccountId) {

        Map paymentAuthorization = new HashMap();
        paymentAuthorization.put("partnerLocationId", partnerLocationId);
        paymentAuthorization.put("providerTransactionId", providerTransactionId);
        paymentAuthorization.put("partnerTransactionId", partnerTransactionId);
        paymentAuthorization.put("totalProducts", 1);
        paymentAuthorization.put("totalPaymentAmount", totalPaymentAmount);
        paymentAuthorization.put("paymentMethod", paymentMethod);
        paymentAuthorization.put("paymentMeanId", paymentMeanId);


        paymentAuthorization.put("products",
                Lists.newArrayList(ImmutableMap.of(
                        "productAccountId", productAccountId,
                        "paymentAmount", totalPaymentAmount,
                        "paymentDate", FAST_DATE_FORMAT.format(System.currentTimeMillis()))));

        try {
            log.trace("partner api authorize request {}", paymentAuthorization);

            ResponseEntity<Map> responseEntity = partnerApiRestTemplate.exchange(url + "/payments", HttpMethod.POST,
                    new HttpEntity(paymentAuthorization), Map.class, new HashMap<>());

            log.trace("partner api authorize response {}", responseEntity.getBody());

            return Optional.of(new PartnerApiAuthorizationResponse(responseEntity.getBody()));
        } catch (Exception e) {
            log.trace("unable to call partner api ", e);
            return Optional.empty();
        }
    }

    public boolean submitPayment(String partnerLocationId,
                                 String providerTransactionId,
                                 String partnerTransactionId,
                                 Double totalPaymentAmount,
                                 String paymentTransactionId,
                                 String productAccountId,
                                 String authorizationId,
                                 Double transactionFee,
                                 String paymentMethod,
                                 String source) {

        Map payment = new HashMap();
        payment.put("partnerLocationId", partnerLocationId);
        payment.put("providerTransactionId", providerTransactionId);
        payment.put("partnerTransactionId", partnerTransactionId);
        payment.put("totalProducts", 1);
        payment.put("totalPaymentAmount", totalPaymentAmount);
        payment.put("paymentMethod", paymentMethod);
        payment.put("paymentMeanId", 0);
        payment.put("paymentTransactionId", paymentTransactionId);
        payment.put("products",
                Lists.newArrayList(ImmutableMap.of(
                        "productAccountId", productAccountId,
                        "paymentAmount", totalPaymentAmount,
                        "paymentDate", FAST_DATE_FORMAT.format(System.currentTimeMillis()),
                        "authorizationId", authorizationId,
                        "transactionFee", transactionFee)));
        payment.put("source", source);

        try {
            log.trace("partner api submit payment request {}", payment);
            ResponseEntity<Map> responseEntity = partnerApiRestTemplate.exchange(url + "/payments/" + paymentTransactionId, HttpMethod.PUT,
                    new HttpEntity(payment), Map.class, new HashMap<>());
            log.trace("partner api submit payment response {}", responseEntity.getBody());
            return true;

        } catch (Exception e) {
            log.error("unable to submit payment to partner api"
                    + " paymentTransactionId " + paymentTransactionId
                    + " productAccountId " + productAccountId
                    + " authorizationId " + authorizationId
                    + " transactionFee " + transactionFee, e);

            return false;
        }
    }

    public Optional<Map<String, Object>> getAllPartyProducts(Integer partyId) {
        Optional<Map<String, Object>> products = Optional.empty();
        try {
            log.trace("get all party products for partyId {}", partyId);
            ResponseEntity<Map> responseEntity = partnerApiRestTemplate.getForEntity(url + "/parties/" + partyId + "/products", Map.class);
            log.trace("get all party products response {}", responseEntity.getBody());
            products = Optional.of(responseEntity.getBody());
        } catch (Exception e) {
            log.error("unable get products for party " + partyId, e);
        }
        return products;
    }

    public Optional<Map<String, Object>> getPartyProductAccount(Integer partyId, String productAccountId) {
        Optional<Map<String, Object>> partyProductAccount = Optional.empty();

        Optional<Map<String, Object>> allPartyProducts = getAllPartyProducts(partyId);

        if (allPartyProducts.isPresent()) {
            Map<String, Object> responseBody = allPartyProducts.get();
            if (responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data.containsKey("productAccounts")) {
                    List<Map<String, Object>> productAccounts = (List<Map<String, Object>>) data.get("productAccounts");

                    partyProductAccount = productAccounts
                            .stream()
                            .filter(pa -> pa.containsKey("productAccountId") && ((String) pa.get("productAccountId")).equals(productAccountId))
                            .findFirst();
                }
            }
        }
        return partyProductAccount;
    }

    public Optional<Map<String, Object>> disbursementAuthorization(String partnerLocationId,
                                                                   String applicationId,
                                                                   String paymentMethod,
                                                                   Double disbursementAmount,
                                                                   String disbursementDate) {

        Optional<Map<String, Object>> disbursementAuthorization = Optional.empty();

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("partnerUserId", "string");
            request.put("partnerLocationId", partnerLocationId);
            request.put("partnerStationId", "string");
            request.put("partnerTransactionId", "string");
            request.put("applicationId", applicationId);
            request.put("disbursementAmount", disbursementAmount);
            request.put("disbursementDate", disbursementDate);
            request.put("paymentMethod", paymentMethod);

            log.trace("partner api disbursement authorization {}", request);

            ResponseEntity<Map> responseEntity = partnerApiRestTemplate.postForEntity(
                    url + "/products/loan/applications/" + applicationId + "/disbursements/fund", request, Map.class);
            log.trace("partner api disbursement authorization response {}", responseEntity.getBody());
            disbursementAuthorization = Optional.of(responseEntity.getBody());
        } catch (Exception e) {
            log.error("unable to loan app " + applicationId, e);
        }

        return disbursementAuthorization;
    }

    public Optional<Map<String, Object>> createProspect() {
        Optional<Map<String, Object>> createProspectResponse = Optional.empty();

        /*
        {"partnerUserId":"34999",
        "campaignId":"",
        "address2":"",
        "partnerLocationId":"1219",
        "tenantExternalId":"1231",
        "firstName":"Tung",
        "lastName":"Truong",
        "phoneNumber":"16782589088",
        "country":"",
        "postalCode":"70000",
        "partnerStationId":"",
        "partnerTransactionId":"123456789",
        "consent":true,
        "source":"LEAD_GEN",
        "preferredLanguage":"es-US","consentIp":"104.172.189.218","tenantId":164}
         */
        Map<String, Object> prospect = new HashMap<>();
        prospect.put("phoneNumber", "8058505077");
        prospect.put("partnerLocationId", "12345");
        prospect.put("consent", true);
        prospect.put("firstName", "Quency");
        prospect.put("lastName", "Adams");
        prospect.put("dateOfBirth", "1767-07-11");
        prospect.put("address1", "1515 CANNON PKWY");
        prospect.put("city", "Ronake");
        prospect.put("state", "TX");
        prospect.put("postalCode", "76262");
        prospect.put("email", "john@email.com");
        prospect.put("preferredLanguage", "en-US");
        prospect.put("tenantExternalId", "123456789");
        prospect.put("source", "LEAD_GEN");

        try {
            log.trace("partner api create prospect {}", prospect);

            ResponseEntity<Map> responseEntity = partnerApiRestTemplate.postForEntity(
                    url + "/prospect", prospect, Map.class);
            log.trace("partner api create prospect response {}", responseEntity.getBody());
            createProspectResponse = Optional.of(responseEntity.getBody());
        } catch (Exception e) {
            log.error("unable to create prospect ", e);
        }
        return createProspectResponse;
    }

    ;
}
