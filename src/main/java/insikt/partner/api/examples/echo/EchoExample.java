package insikt.partner.api.examples.echo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import insikt.partner.api.PartnerApiClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class EchoExample implements CommandLineRunner {

    @Autowired
    private PartnerApiClient partnerApiClient;

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Running Echo Example.**********************************************");
        partnerApiClient.echo("Hello");
        log.debug("*********************************Done**********************************************");

    }
}
