package insikt.partner.api.examples.prospect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import insikt.partner.api.PartnerApiClient;
import insikt.partner.api.PublicApiClient;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
@Order(4)
public class ProspectExample implements CommandLineRunner {

    @Autowired
    private PartnerApiClient partnerApiClient;

    @Autowired
    private PublicApiClient publicApiClient;

    @Override
    public void run(String... args) throws Exception {
        createPartnerProspect();
    }

    private void createPartnerProspect() {
        log.debug("*********************************Running Prospect Example.**********************************************");
        Optional<Map<String, Object>> response = partnerApiClient.createProspect();
        log.debug("*********************************Done..**********************************************");
    }

    private void createInternalProspect() {
        log.debug("*********************************Running Public Prospect Example.**********************************************");
        publicApiClient.createProspect();
        log.debug("*********************************Done..**********************************************");
    }
}
