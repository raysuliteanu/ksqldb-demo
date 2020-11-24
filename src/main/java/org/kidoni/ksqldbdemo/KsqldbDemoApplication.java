package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.entity.ServerInfo;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KsqldbDemoApplication {

    public static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8088";

    public static void main(String[] args) {
        SpringApplication.run(KsqldbDemoApplication.class, args);
    }

    @Component
    @Slf4j
    static class Runner implements CommandLineRunner {

        @Override
        public void run(String... args) {
            String serverAddress = args.length == 1 ? args[0] : DEFAULT_SERVER_ADDRESS;

            try (KsqlRestClient restClient = KsqlRestClient.create(serverAddress, Collections.emptyMap(), Collections.emptyMap(), Optional.empty())) {
                RestResponse<ServerInfo> serverInfo = restClient.getServerInfo();
                if (serverInfo.isSuccessful()) {
                    ServerInfo serverInfoResponse = serverInfo.getResponse();
                    log.info("cluster_id: {}, ksql_service_id: {}, server_status: {}, version: {}",
                            serverInfoResponse.getKafkaClusterId(), serverInfoResponse.getKsqlServiceId(), serverInfoResponse.getServerStatus(), serverInfoResponse.getVersion());
                }
                else {
                    KsqlErrorMessage errorMessage = serverInfo.getErrorMessage();
                    log.error("Error: {} Code: {}", errorMessage.getMessage(), errorMessage.getErrorCode());
                }
            }
        }
    }
}
