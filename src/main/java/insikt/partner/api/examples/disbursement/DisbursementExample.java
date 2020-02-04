package insikt.partner.api.examples.disbursement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import insikt.partner.api.PartnerApiClient;
import lombok.extern.slf4j.Slf4j;

//@Component
@Order(3)
@Slf4j
public class DisbursementExample implements CommandLineRunner {

    @Autowired
    private PartnerApiClient partnerApiClient;

    @Autowired
    private DisbursementExampleProperties disbursementExampleProperties;

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Disbursement Example..**********************************************");

        log.debug("Authorize Disbursement");
        partnerApiClient.disbursementAuthorization(disbursementExampleProperties.getPartnerLocationId(),
                disbursementExampleProperties.getApplicationId(),
                disbursementExampleProperties.getPaymentMethod(),
                disbursementExampleProperties.getDisbursementAmount(),
                "2018-08-15"
        );

        log.debug("*********************************Done**********************************************");

    }
}
