package org.kidoni.ksqldbdemo;

import java.util.Map;
import lombok.Data;

@Data
public class CreateStreamRequest {
    String streamName;
    Map<String, String> columns;
    String sourceTopicName;
    boolean createTopic;
    int partitions;
    short replicas;
    String keyColumn;
    String valueFormat;
}
