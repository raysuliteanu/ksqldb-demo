package org.kidoni.ksqldbdemo;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@Testcontainers
@Tag("testcontainers")
@AutoConfigureWebTestClient
@SpringBootTest
class KsqldbDemoApplicationTests {
    @Container
    private final static DockerComposeContainer<?> dockerComposeContainer =
            new DockerComposeContainer<>(new File("src/test/resources/compose-test.yml"))
                    .withServices("zookeeper", "broker", "ksqldb-server")
                    .withExposedService("ksqldb-server", 8088, Wait.forHealthcheck())
                    .withLocalCompose(true);
    public static final String TEST_TOPIC_NAME = "input";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void infoHandler() {
        webTestClient.get()
                     .uri("/info")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(String.class)
                     .consumeWith(result -> {
                         String responseBody = result.getResponseBody();
                         assertThat(responseBody).contains("cluster_id", "ksql_service_id", "server_status", "version");
                     });

    }

    @Test
    void createStreamRequest() {
        CreateStreamRequest createStreamRequest = RequestGenerator.generateCreateStreamRequest();

        webTestClient.post()
                     .uri("/stream")
                     .bodyValue(createStreamRequest)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(RequestStatus.class)
                     .value(RequestStatus::getMessage, equalTo(KsqlDbRequestHandler.REQUEST_SUCCESS_MESSAGE));
    }
}
