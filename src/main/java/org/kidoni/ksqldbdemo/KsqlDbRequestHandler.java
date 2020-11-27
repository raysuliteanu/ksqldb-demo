package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.entity.ServerInfo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Slf4j
public class KsqlDbRequestHandler {
    static final String REQUEST_SUCCESS_MESSAGE = "request processed successfully";
    static final String CREATE_STREAM_STATEMENT = "CREATE STREAM %s AS SELECT %s FROM %s EMIT CHANGES";

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

    public Mono<ServerResponse> createStream(ServerRequest serverRequest) {
        Mono<RequestStatus> result = serverRequest
                .bodyToMono(CreateStreamRequest.class)
                .map(this::executeCreateStreamRequest)
                .map(this::buildRequestStatus);

        return ServerResponse.ok().body(result, RequestStatus.class);
    }

    RestResponse<KsqlEntityList> executeCreateStreamRequest(CreateStreamRequest createStreamRequest) {
        String ksql = format(CREATE_STREAM_STATEMENT,
                createStreamRequest.streamName, generateColumns(createStreamRequest.columns), createStreamRequest.sourceTopicName);
        return ksqlRestClient.makeKsqlRequest(ksql);
    }

    RequestStatus buildRequestStatus(RestResponse<KsqlEntityList> restResponse) {
        String message = restResponse.isSuccessful() ? REQUEST_SUCCESS_MESSAGE : restResponse.getErrorMessage().getMessage();
        return new RequestStatus(message, restResponse.getResponse());
    }

    private String generateColumns(Map<String, String> columns) {
        return StringUtils.collectionToCommaDelimitedString(columns.keySet());
    }
}
