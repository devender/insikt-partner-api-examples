package insikt.partner.api.examples;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiClient {

    private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    @Autowired
    private RestTemplate partnerApiRestTemplate;

    @Value("${insikt.partner.api.url}")
    private String url;

    public Map echo(final String message) {
        Map<String, Object> apiParams = new HashMap();
        apiParams.put("message", message);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url + "/echo");
        for (String s : apiParams.keySet()) {
            builder.queryParam(s, apiParams.get(s));
        }

        log.trace("Sending: echo {}", builder.build(false).toUri());

        ResponseEntity<Map> responseEntity = partnerApiRestTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, Map.class);

        log.trace("Returned: response {} ", responseEntity.getBody());

        return responseEntity.getBody();
    }
}
