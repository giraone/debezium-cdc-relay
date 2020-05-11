package com.giraone.cdc.listener;

import com.giraone.cdc.elasticsearch.service.StudentService;
import com.giraone.cdc.utils.Operation;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class CDCListener {

    /**
     * Handle to the Service layer, which interacts with ElasticSearch.
     */
    private final StudentService studentService;

    private CDCListener(Configuration configuration, StudentService studentService) throws IOException {

        this.studentService = studentService;

        try (DebeziumEngine<SourceRecord> engine = DebeziumEngine.create(Connect.class)
            .using(configuration.asProperties())
            .notifying(this::handleEvent)
            .build()) {

            Executors.newSingleThreadExecutor().execute(engine);

            // Run the engine asynchronously ...
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);
        }
    }

    /**
     * This method is invoked when a transactional action is performed on any of the tables that were configured.
     * @param sourceRecord CDC source record
     */
    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();

        if (sourceRecordValue == null) return;

        Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));
        if (operation == null || operation == Operation.READ) return;

        Map<String, Object> message;
        String record = AFTER; // For Update & Insert operations.

        if (operation == Operation.DELETE) {
            record = BEFORE; // For Delete operations.
        }

        // Build a map with all row data received.
        Struct struct = (Struct) sourceRecordValue.get(record);
        message = struct.schema().fields().stream()
            .map(Field::name)
            .filter(fieldName -> struct.get(fieldName) != null)
            .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
            .collect(toMap(Pair::getKey, Pair::getValue));

        // Call the service to handle the data change.
        this.studentService.maintainReadModel(message, operation);
        log.info("Data Changed: {} with Operation: {}", message, operation.name());
    }
}
