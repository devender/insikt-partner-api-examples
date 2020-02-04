package insikt.partner.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PublicApiClient {

    @Value("${insikt.partner.api.url}")
    private String url;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    public void createProspect() {

        Map<String, Object> prospect = new HashMap<>();
        prospect.put("partnerLocationId", "1027");
        prospect.put("token", "BW5SPGQUr25Vhz5wv2Niu1CYQcfZYlkBq56auHixbQ4cNNELWktE0sI0of1GTy8");
        prospect.put("phoneNumber", "8058505077");

        try {
            log.trace("partner api create prospect {}", prospect);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                    url + "/prospect/internal", prospect, Map.class);

            log.trace("partner api create prospect response {}", responseEntity.getBody());
        } catch (Exception e) {
            log.error("unable to create prospect ", e);
        }
    }

    public void ocrolusCallback() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "VERIFICATION_COMPLETE");
        payload.put("book_pk", 11917);
        payload.put("uploaded_doc_pk", 57733);

        try {
            log.trace("partner api ocrolus callback {}", payload);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                    url + "/income/provider/ocrolus/callback", payload, Map.class);

            log.trace("partner api ocrolus callback response {}", responseEntity.getBody());
        } catch (Exception e) {
            log.error("unable to send callback ", e);
        }
    }
}
