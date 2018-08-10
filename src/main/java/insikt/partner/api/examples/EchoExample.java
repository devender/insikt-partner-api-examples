package insikt.partner.api.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import insikt.partner.api.ApiClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class EchoExample implements CommandLineRunner {

    @Autowired
    private ApiClient apiClient;

    @Override
    public void run(String... args) throws Exception {
        log.debug("*********************************Running Echo Example.**********************************************");
        apiClient.echo("Hello");
        log.debug("*********************************Done**********************************************");

    }
}
