package insikt.partner.api.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EchoCommand implements CommandLineRunner {

    @Autowired
    private ApiClient apiClient;

    @Override
    public void run(String... args) throws Exception {
        apiClient.echo("Hello");
    }
}
