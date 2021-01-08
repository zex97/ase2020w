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
    @Query(value = "SELECT * FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId ORDER BY f.f_id", nativeQuery = true)
    List<Flashcard> findByDeckId(@Param("deckId") long deckId);

    /**
     * Find all flashcards of one deck by deck id.
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT * FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId ORDER BY f.next_due_date LIMIT :size", nativeQuery = true)
    List<Flashcard> findByDeckIdOrderByDueDateLimitSize(@Param("deckId") long deckId, @Param("size") int size);

    /**
     * Find all flashcards that are due now of one deck by deck id.
     *
     * @param deckId of the deck containing flashcards
     * @return all flashcards belonging to specified deck
     */
    @Query(value = "SELECT * FROM flashcard f JOIN flashcards_assignment f_a ON f.f_id=f_a.flashcard_id WHERE f_a.deck_id = :deckId AND f.next_due_date <= :now", nativeQuery = true)
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

    /**
     * Get all decks a flashcard is assigned to
     *
     * @param cardId - id of the flashcard
     */
    @Query(value = "SELECT fa.deck_id FROM flashcards_assignment fa WHERE fa.flashcard_id=:cardId", nativeQuery = true)
    List<Long> getAllAssignments(long cardId);

    /**
     * Add a document reference to a flashcard
     *
     * @param cardId of the flashcard
     * @param documentId of the document the flashcard is referencing
     */
    @Modifying
    @Query(value = "INSERT INTO flashcards_reference VALUES(:documentId, :cardId)", nativeQuery = true)
    @Transactional
    void addReference(long cardId, long documentId);

    /**
     * Remove a document reference from a flashcard
     *
     * @param cardId of the flashcard
     * @param documentId of the document the flashcard is referencing
     */
    @Modifying
    @Query(value = "DELETE FROM flashcards_reference fr WHERE fr.flashcard_id=:cardId AND fr.document_id=:documentId", nativeQuery = true)
    @Transactional
    void removeReference(long cardId, long documentId);

    /**
     * Get all document references for a flashcard
     *
     * @param cardId - id of the flashcard
     */
    @Query(value = "SELECT fr.document_id FROM flashcards_reference fr WHERE fr.flashcard_id=:cardId", nativeQuery = true)
    List<Long> getAllReferences(long cardId);

}
