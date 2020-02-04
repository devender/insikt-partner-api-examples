package insikt.partner.api.examples.disbursement;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "example.disbursement")
public class DisbursementExampleProperties {
    String partnerLocationId;
    String applicationId;
    String paymentMethod;
    Double disbursementAmount;
}
