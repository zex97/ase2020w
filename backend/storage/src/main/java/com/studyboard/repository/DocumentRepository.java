package com.studyboard.repository;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

    Optional<Document> findByFilePath(String filePath);

}
