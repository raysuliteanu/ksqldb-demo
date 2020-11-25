package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.entity.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Slf4j
public class KsqlDbRequestHandler {

    private final KsqlRestClient ksqlRestClient;

    public KsqlDbRequestHandler(KsqlRestClient ksqlRestClient) {
        this.ksqlRestClient = ksqlRestClient;
    }

    public Mono<ServerResponse> info(ServerRequest serverRequest) {
        RestResponse<ServerInfo> serverInfo = ksqlRestClient.getServerInfo();
        if (serverInfo.isSuccessful()) {
            ServerInfo serverInfoResponse = serverInfo.getResponse();
            return ServerResponse.ok().bodyValue(format("cluster_id: %s, ksql_service_id: %s, server_status: %s, version: %s",
                    serverInfoResponse.getKafkaClusterId(), serverInfoResponse.getKsqlServiceId(), serverInfoResponse.getServerStatus(), serverInfoResponse.getVersion()));
        }
        else {
            KsqlErrorMessage errorMessage = serverInfo.getErrorMessage();
            String body = format("Error: %s Code: %s", errorMessage.getMessage(), errorMessage.getErrorCode());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(body);
        }
    }
}
