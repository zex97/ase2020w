package com.studyboard.repository;

import com.studyboard.model.Flashcard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {

    /**
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT f.f_id, f.question, f.answer, f.confidence_level FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId", nativeQuery = true)
    List<Flashcard> findByDeck(@Param("deckId") long deckId);

    /**
     *
     * @param id of the flashcard
     * @return the flashcard with the specified id
     */
    Flashcard findFlashcardById(long id);

    /**
     * Assign a flashcard to a deck
     *
     * @param deckId of the deck flashcard belongs to
     * @param cardId - id of the flashcard
     */
    @Modifying
    @Query(value = "INSERT INTO flashcards_assignment VALUES(:deckId, :cardId)", nativeQuery = true)
    @Transactional
    void assignFlashcard(long deckId, long cardId);
}
