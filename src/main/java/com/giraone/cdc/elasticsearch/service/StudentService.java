package com.giraone.cdc.elasticsearch.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.giraone.cdc.elasticsearch.entity.Student;
import com.giraone.cdc.elasticsearch.repository.StudentRepository;
import com.giraone.cdc.utils.Operation;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Service interface that masks the caller from the implementation that fetches
 * / acts on Student related data.
 */
@Service
public class StudentService {

    /**
     * Handle to ElasticSearch
     */
    private final StudentRepository studentRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostConstruct
    void init() {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Updates/Inserts/Delete student data.
     *
     * @param studentData CDC event as map
     * @param operation CDC event operation
     */
    public void maintainReadModel(Map<String, Object> studentData, Operation operation) {

        final Student student = mapper.convertValue(studentData, Student.class);

        if (Operation.DELETE.name().equals(operation.name())) {
            studentRepository.deleteById(student.getId());
        } else {
            studentRepository.save(student);
        }
    }
}
