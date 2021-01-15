package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;

import static io.confluent.ksql.rest.client.RestResponse.successful;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@ActiveProfiles("test")
@WebFluxTest
@Import(WebfluxRouterConfiguration.class)
class KsqlDbRequestHandlerTest {

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;

    @MockBean
    private KsqlRestClient ksqlRestClient;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
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

        String expectedSql = "CREATE STREAM test (first int,second string) WITH (kafka_topic = 'input', partitions = 1, value_format = 'json');";
        given(ksqlRestClient.makeKsqlRequest(eq(expectedSql))).willReturn(restResponse);

        CreateStreamRequest createStreamRequest = RequestGenerator.generateCreateStreamRequest();

        webTestClient.post()
                .uri("/stream")
                .bodyValue(createStreamRequest)
                .exchange()
                .expectStatus().isOk()
                     .expectBody(RequestStatus.class)
                     .value(equalTo(new RequestStatus(KsqlDbRequestHandler.REQUEST_SUCCESS_MESSAGE, ksqlEntities)));
    }
}