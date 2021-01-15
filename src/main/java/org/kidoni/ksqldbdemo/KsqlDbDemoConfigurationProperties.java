package org.kidoni.ksqldbdemo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "org.kidoni.ksqldb")
public class KsqlDbDemoConfigurationProperties {
    public static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8088";

    /**
     * the URL of the ksqlDb server
     */
    String ksqlDbServer = DEFAULT_SERVER_ADDRESS;
}
