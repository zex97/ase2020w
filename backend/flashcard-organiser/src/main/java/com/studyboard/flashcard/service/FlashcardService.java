package com.studyboard.flashcard.service;

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
     * Find a single deck by id
     *
     * @param username of the user who created the deck
     * @param deckId of the deck
     * @return the deck with the corresponding id
     */
    Deck getOneDeck(String username, long deckId);

    /**
     * Create a single deck
     *
     * @param username of the user who is creating the deck
     * @param deck with all the necessary information about a deck
     */
    void createDeck(String username, Deck deck);

    /**
     * Update a single deck
     *
     * @param username of the user who created the deck
     * @param deck   - with the information to be updated
     * @return updated deck
     */
    Deck updateDeckName(String username, Deck deck);

    /**
     * Find all flashcards user created and assigned to one deck
     *
     * @param deckId of the deck in which flashcards are
     * @return list of all flashcards
     */
    List<Flashcard> getAllFlashcardsOfDeck(long deckId);

    /**
     * Find a single flashcard by id
     *
     * @param deckId of the deck in which flashcard is
     * @param flashcardId of the flashcard
     * @return the flashcard with the corresponding id
     */
    Flashcard getOneFlashcard(long deckId, long flashcardId);

    /**
     * Create a single flashcard
     *
     * @param deckId of the deck in which flashcard will be
     * @param flashcard entity with all the necessary information
     */
    void createFlashcard(long deckId, Flashcard flashcard);

    /**
     * Delete a single deck with all of its flashcards
     *
     * @param userId of the user who created and wants to delete deck
     * @param deckId of the deck that should be deleted
     */
    void deleteDeck(long userId, long deckId);

    /**
     * Delete a single flashcard
     *
     * @param deckId of the deck in which flashcard is
     * @param flashcardId of the flashcard that should be deleted
     */
    void deleteFlashcard(long deckId, long flashcardId);

}
