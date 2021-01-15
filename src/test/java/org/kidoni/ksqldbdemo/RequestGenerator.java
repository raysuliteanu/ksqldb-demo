package org.kidoni.ksqldbdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.HashMap;
import java.util.Map;

public class RequestGenerator {

    private static final ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static void main(String[] args) throws JsonProcessingException {
        CreateStreamRequest createStreamRequest = generateCreateStreamRequest();

        System.out.println(objectWriter.writeValueAsString(createStreamRequest));
    }

    public static CreateStreamRequest generateCreateStreamRequest() {
        CreateStreamRequest createStreamRequest = new CreateStreamRequest();
        createStreamRequest.setCreateTopic(true);
        createStreamRequest.setStreamName("test");
        createStreamRequest.setSourceTopicName("input");
        createStreamRequest.setPartitions(1);
        createStreamRequest.setValueFormat("json");
        Map<String, String> columns = new HashMap<>();
        columns.put("first", "int");
        columns.put("second", "string");
        createStreamRequest.setColumns(columns);
        return createStreamRequest;
    }
}
