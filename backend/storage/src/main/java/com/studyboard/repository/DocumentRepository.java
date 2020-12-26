package com.studyboard.repository;

import com.studyboard.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

}
