package com.studyboard.repository;

import com.studyboard.model.Flashcard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {

    List<Flashcard> findByDeckId(long deckId);

    Flashcard findFlashcardById(long id);
}
