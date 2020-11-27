package org.kidoni.ksqldbdemo;

import io.confluent.ksql.rest.entity.KsqlEntityList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatus {
    String message;
    KsqlEntityList ksqlEntities;
}
