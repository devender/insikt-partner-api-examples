package insikt.partner.api.examples;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "example.payment")
public class PaymentExampleProperties {

    private String partnerLocationId;
    private String providerTransactionId;
    private String partnerTransactionId;
    private Double totalPaymentAmount;
    private String paymentMethod;
    private String productAccountId;
    private Integer paymentMeanId;

}
