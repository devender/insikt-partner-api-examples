package insikt.partner.api.examples;

import com.google.common.collect.Lists;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "partnerApiRestTemplate")
    public RestTemplate partnerApiRestTemplate(@Value("${insikt.partner.api.privateKey}") String privateKey,
                                               @Value("${insikt.partner.api.subject}") String subject,
                                               @Value("${insikt.partner.api.token.expiry.minutes}") Integer expiryMin,
                                               @Value("${insikt.partner.api.issuer}") String issuer,
                                               @Value("${insikt.partner.api.audience}") String audience,
                                               @Value("${insikt.partner.api.ssl_ignore}") boolean ignoreSsl) {

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        if (ignoreSsl) {
            //ignore ssl errors
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .build();
            requestFactory.setHttpClient(httpClient);
        }

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        //use json
        restTemplate.getInterceptors().add(
                (HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) -> {
                    httpRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return execution.execute(httpRequest, body);
                });

        //use jwt
        restTemplate.getInterceptors().add(
                (HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) -> {
                    String bearerToken = JwtUtil.generateBearer(privateKey, Lists.newArrayList(audience), issuer, subject, TimeUnit.MINUTES.toMillis(expiryMin), new HashMap<>(0));
                    httpRequest.getHeaders().set("Authorization", "Bearer " + bearerToken);
                    return execution.execute(httpRequest, body);
                });

        restTemplate.setMessageConverters(Lists.newArrayList(new MappingJackson2HttpMessageConverter(ObjectMapperFactory.newObjectMapper())));

        return restTemplate;
    }

}
