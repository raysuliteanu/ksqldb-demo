package org.kidoni.ksqldbdemo;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
class KsqldbDemoApplicationTests {
    @Container
    public static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer<>(new File("src/test/resources/compose-test.yml"))
                    .withServices("zookeeper", "broker", "ksqldb-server")
                    .withExposedService("ksqldb-server", 8088,
                            Wait.forHealthcheck())
                    .withLocalCompose(true);

    @Test
    void contextLoads() {
    }

}
