package insikt.partner.api.examples.loanapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import insikt.partner.api.ObjectMapperFactory;
import insikt.partner.api.PartnerApiClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class LoanAppSummaryExample implements CommandLineRunner {

    @Autowired
    private PartnerApiClient partnerApiClient;

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Running LoanApp Summary Example.**********************************************");
        Optional<Map<String, Object>> response = partnerApiClient.getAllPartyProducts(94968);
        Map<String, Object> data = (Map<String, Object>) response.get().get("data");
        List<Map<String, Object>> apps = (List<Map<String, Object>>) data.get("applications");

        apps.forEach(map -> log.debug(constructSourceJsonString(map)));

        log.debug("*********************************Done**********************************************");
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
