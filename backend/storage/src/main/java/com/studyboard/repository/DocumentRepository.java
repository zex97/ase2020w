package com.studyboard.repository;

import com.studyboard.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

    /**
     * Find a single document entry by id.
     *
     * @param documentId is of the document entry
     * @return document object with specified id
     */
    Document findDocumentById(long documentId);

    /**
     * find all documents containing given string in the filename
     * @param spaceId space to search in
     * @param searchParam string to be searched for
     * @return list of all matched documents
     */
    List<Document> findBySpaceIdAndNameContaining(Long spaceId, String searchParam);
}
