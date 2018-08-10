package insikt.partner.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiClient {

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    @Autowired
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
}
