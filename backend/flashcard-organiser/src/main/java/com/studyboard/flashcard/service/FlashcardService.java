package com.studyboard.flashcard.service;

import com.studyboard.flashcard.exception.FlashcardConstraintException;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;

import java.util.List;

public interface FlashcardService {

    /**
     * Find all decks user created order by the last time they were used for revision
     *
     * @param username of the user who created the deck
     * @return list of all decks order by the last time used attribute
     */
    List<Deck> getAllDecks(String username);

    /**
     * Find a deck with the given id
     *
     * @param deckId of the deck to find
     * @return deck with the specified id
     */
    Deck findDeckById(Long deckId);

    /**
     * Create a single deck
     *
     * @param deck with all the necessary information about a deck
     */
    void createDeck(Deck deck);

    /**
     * Update a single deck
     *
     * @param deck - with the information to be updated
     * @return updated deck
     */

    Deck updateDeckName(Deck deck);

    /**
     * Find all flashcards user created and assigned to one deck
     *
     * @param deckId of the deck in which flashcards are
     * @return list of all flashcards
     */
    List<Flashcard> getAllFlashcardsOfDeck(long deckId);

    /**
     * Get as many flashcards from a deck as the user choose
     *
     * @param deckId of the deck flashcards belong to
     * @param size   - amount of flashcards to revise
     * @return chosen number of flashcards from a specified deck
     */
    List<Flashcard> getFlashcardsForRevision(long deckId, int size);


    /**
     * Find a single flashcard by id
     *
     * @param flashcardId of the flashcard
     * @return the flashcard with the corresponding id
     */
    Flashcard getOneFlashcard(long flashcardId);

    /**
     * Create a single flashcard
     *
     * @param flashcard entity with all the necessary information
     */
    Flashcard createFlashcard(Flashcard flashcard);

    /**
     * Assign the flashcard to decks it belongs to
     *
     * @param flashcardId of the card that was created
     * @param decks       with the id's of decks flashcard is assigned to
     */
    void assignFlashcard(long flashcardId, String decks);

    /**
     * Delete a single deck with all of its flashcards
     *
     * @param deckId of the deck that should be deleted
     */
    void deleteDeck(long deckId);

    /**
     * Delete a single flashcard
     *
     * @param deckId      of the deck in which flashcard is
     * @param flashcardId of the flashcard that should be deleted
     */
    void deleteFlashcard(long deckId, long flashcardId);

    /**
     * Edit a single flashcard in the deck
     *
     * @param flashcard entity that needs to be edited
     * @return flashcard object with the all the changes
     * @throws FlashcardConstraintException when confidence level is outside 1-5 range of values
     */
    Flashcard editFlashcard(Flashcard flashcard) throws FlashcardConstraintException;

}
