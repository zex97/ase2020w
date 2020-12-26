package com.studyboard.repository;

import com.studyboard.model.Flashcard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {

    /**
     * Find all flashcards of one deck by deck id.
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT f.f_id, f.question, f.answer, f.easiness, f.interval, f.correctness_streak, f.next_due_date FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId", nativeQuery = true)
    List<Flashcard> findByDeckId(@Param("deckId") long deckId);

    /**
     * Find all flashcards of one deck by deck id.
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT f.f_id, f.question, f.answer, f.easiness, f.interval, f.correctness_streak, f.next_due_date FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId ORDER BY f.next_due_date LIMIT :size", nativeQuery = true)
    List<Flashcard> findByDeckIdOrderByDueDateLimitSize(@Param("deckId") long deckId, @Param("size") int size);

    /**
     * Find all flashcards that are due now of one deck by deck id.
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT f.f_id, f.question, f.answer, f.easiness, f.interval, f.correctness_streak, f.next_due_date FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId AND f.next_due_date <= :now", nativeQuery = true)
    List<Flashcard> findAllDueToday(@Param("deckId") long deckId, @Param("now") LocalDateTime now);

    /**
     * Find a single flashcard by id.
     *
     * @param id is of the flashcard entry
     * @return flashcard object with specified id
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

    /**
     * Unassign a flashcard from a deck
     *
     * @param deckId of the deck flashcard belongs to
     * @param cardId - id of the flashcard
     */
    @Modifying
    @Query(value = "DELETE FROM flashcards_assignment fa WHERE fa.flashcard_id=:cardId AND fa.deck_id=:deckId", nativeQuery = true)
    @Transactional
    void removeAssignment(long deckId, long cardId);
}
