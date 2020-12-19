package com.studyboard.repository;

import com.studyboard.model.Flashcard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {

    /**
     * Find all flashcards of one deck by deck id.
     *
     * @param deckId is of the deck where all flashcards are
     * @return flashcard objects of a deck with specified id
     */
    List<Flashcard> findByDeckId(long deckId);

    /**
     * Find a single flashcard by id.
     *
     * @param id is of the flashcard entry
     * @return flashcard object with specified id
     */
    Flashcard findFlashcardById(long id);
}
