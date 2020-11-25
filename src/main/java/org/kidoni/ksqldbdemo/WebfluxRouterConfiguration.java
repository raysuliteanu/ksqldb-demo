package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import java.util.Collections;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableWebFlux
@Configuration
public class WebfluxRouterConfiguration {
    @Bean
    public KsqlDbRequestHandler ksqlDbRequestHandler(KsqlRestClient ksqlRestClient) {
        return new KsqlDbRequestHandler(ksqlRestClient);
    }

    @Bean
    public RouterFunction<ServerResponse> helloRouterFunction(KsqlDbRequestHandler ksqlDbRequestHandler) {
        return RouterFunctions.route()
                              .GET("/info", ksqlDbRequestHandler::info)
                              .build();
    }

    public static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8088";

    @Bean
    public KsqlRestClient ksqlRestClient() {
        return KsqlRestClient.create(DEFAULT_SERVER_ADDRESS, Collections.emptyMap(), Collections.emptyMap(), Optional.empty());
    }
}
