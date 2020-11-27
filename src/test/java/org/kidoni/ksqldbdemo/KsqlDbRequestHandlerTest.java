package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.confluent.ksql.rest.client.RestResponse.successful;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@WebFluxTest
@Import(WebfluxRouterConfiguration.class)
class KsqlDbRequestHandlerTest {

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;

    @MockBean
    private KsqlRestClient ksqlRestClient;

    private WebTestClient webTestClient;

    @BeforeEach
    private void setup() {
        webTestClient = bindToRouterFunction(routerFunction)
                .configureClient()
                // need some time if using a debugger ...
                .responseTimeout(Duration.ofMinutes(10))
                .build();
    }

    @Test
    void createStreamRequest() {
        KsqlEntityList ksqlEntities = new KsqlEntityList(emptyList());
        RestResponse<KsqlEntityList> restResponse = successful(OK.value(), ksqlEntities);

        given(ksqlRestClient.makeKsqlRequest(eq("CREATE STREAM test AS SELECT first,second FROM input EMIT CHANGES"))).willReturn(restResponse);

        CreateStreamRequest createStreamRequest = new CreateStreamRequest();
        createStreamRequest.setStreamName("test");
        createStreamRequest.setSourceTopicName("input");
        Map<String, String> columns = new HashMap<>();
        columns.put("first", "int");
        columns.put("second", "string");
        createStreamRequest.setColumns(columns);

        webTestClient.post()
                     .uri("/stream")
                     .bodyValue(createStreamRequest)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(RequestStatus.class)
                     .value(equalTo(new RequestStatus(KsqlDbRequestHandler.REQUEST_SUCCESS_MESSAGE, ksqlEntities)));
    }
}