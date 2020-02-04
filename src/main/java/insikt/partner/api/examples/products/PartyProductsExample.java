package insikt.partner.api.examples.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import insikt.partner.api.PartnerApiClient;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
@Order(3)
public class PartyProductsExample implements CommandLineRunner {

    @Autowired
    private PartnerApiClient partnerApiClient;

    @Value("${example.party.products.partyId}")
    private Integer partyId;

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Running Get Party Products..**********************************************");
        Optional<Map<String, Object>> response = partnerApiClient.getAllPartyProducts(partyId);
        log.debug("*********************************Done..**********************************************");
    }
}
