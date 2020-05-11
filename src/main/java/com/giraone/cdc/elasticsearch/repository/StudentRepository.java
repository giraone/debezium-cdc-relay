package com.giraone.cdc.elasticsearch.repository;

import com.giraone.cdc.elasticsearch.entity.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * This interface provides handles to database, to perform CRUD operations on the index `STUDENT`.
 * The index is represented by the JPA entity {@link Student}.
 *
 * @see ElasticsearchRepository
 */
@Repository
public interface StudentRepository extends ElasticsearchRepository<Student, UUID> {
}
