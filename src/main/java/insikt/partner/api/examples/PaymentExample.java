package insikt.partner.api.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import insikt.partner.api.ApiClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class PaymentExample implements CommandLineRunner {

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private PaymentExampleProperties paymentExampleProperties;

    @Override
    public void run(String... args) throws Exception {
        log.debug("Running Payment Example..");
        log.debug("{}", paymentExampleProperties);


    }


}
