package org.kidoni.ksqldbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(KsqlDbDemoConfigurationProperties.class)
@SpringBootApplication
public class KsqldbDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KsqldbDemoApplication.class, args);
    }

}
