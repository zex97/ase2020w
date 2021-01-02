package com.studyboard.repository;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

    /**
     * Find a single document entry by id.
     *
     * @param documentId is of the document entry
     * @return document object with specified id
     */
    Document findDocumentById(long documentId);
}
