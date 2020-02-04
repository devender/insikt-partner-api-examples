package insikt.partner.api.examples.payment;

import com.google.common.collect.ImmutableMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import insikt.partner.api.PartnerApiClient;
import insikt.partner.api.ObjectMapperFactory;
import insikt.partner.api.PartnerApiAuthorizationResponse;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
@Order(2)
public class PaymentExample implements CommandLineRunner {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

    @Autowired
    private PartnerApiClient partnerApiClient;

    @Autowired
    private PaymentExampleProperties paymentExampleProperties;

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Running Payment Example..**********************************************");
        log.debug("Properties to use for payment {}", paymentExampleProperties);
        log.debug("Balance before payment {}", getBalance(paymentExampleProperties.getPartyId(), paymentExampleProperties.getProductAccountId()));

        Optional<PartnerApiAuthorizationResponse> partnerApiResponseOptional = partnerApiClient.authorize(paymentExampleProperties.getPartnerLocationId(),
                paymentExampleProperties.getProviderTransactionId(),
                paymentExampleProperties.getPartnerTransactionId(),
                paymentExampleProperties.getTotalPaymentAmount(),
                paymentExampleProperties.getPaymentMethod(),
                paymentExampleProperties.getPaymentMeanId(),
                paymentExampleProperties.getProductAccountId());

        if (partnerApiResponseOptional.isPresent() && partnerApiResponseOptional.get().callSucceded()) {
            log.debug("authorize succeeded , proceeding to submit");
            Double transactionFee = 0.0;

            boolean submitPayment = partnerApiClient.submitPayment(paymentExampleProperties.getPartnerLocationId(),
                    paymentExampleProperties.getProviderTransactionId(),
                    paymentExampleProperties.getPartnerTransactionId(),
                    paymentExampleProperties.getTotalPaymentAmount(),
                    partnerApiResponseOptional.get().getPaymentTransactionId(),
                    paymentExampleProperties.getProductAccountId(),
                    partnerApiResponseOptional.get().getAuthorizationId(),
                    transactionFee,
                    paymentExampleProperties.getPaymentMethod(),
                    constructSourceJsonString(ImmutableMap.of("key1", "value1"))
            );
            log.debug("submit successful ? {}", submitPayment);
        }

        log.debug("Balance after payment {}", getBalance(paymentExampleProperties.getPartyId(), paymentExampleProperties.getProductAccountId()));
        log.debug("*********************************Done..**********************************************");
    }

    private Optional<Double> getBalance(Integer partyId, String productAccountId) {
        Optional<Double> balance = Optional.empty();
        Optional<Map<String, Object>> partyProductAccount = partnerApiClient.getPartyProductAccount(partyId, productAccountId);
        if (partyProductAccount.isPresent() && partyProductAccount.get().containsKey("balance")) {
            balance = Optional.ofNullable((Double) partyProductAccount.get().get("balance"));
        }
        return balance;
    }

    static String constructSourceJsonString(Map<String, Object> params) {
        try {
            return OBJECT_MAPPER.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            log.warn("Unable to generate JSON string for source", e);
            return "{}";
        }
    }
}
