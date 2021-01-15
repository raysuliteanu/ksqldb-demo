package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.client.KsqlRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Optional;

import static java.util.Collections.emptyMap;

@EnableWebFlux
@Configuration
public class WebfluxRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> infoRouterFunction(KsqlDbRequestHandler ksqlDbRequestHandler) {
        return RouterFunctions.route()
                .GET("/info", ksqlDbRequestHandler::info)
                .POST("/stream", ksqlDbRequestHandler::createStream)
                              .build();
    }

    @Bean
    public KsqlDbRequestHandler ksqlDbRequestHandler(KsqlRestClient ksqlRestClient) {
        return new KsqlDbRequestHandler(ksqlRestClient);
    }

    @Bean
    public KsqlRestClient ksqlRestClient(KsqlDbDemoConfigurationProperties configurationProperties) {
        return KsqlRestClient.create(configurationProperties.getKsqlDbServer(), emptyMap(), emptyMap(), Optional.empty());
    }
}
