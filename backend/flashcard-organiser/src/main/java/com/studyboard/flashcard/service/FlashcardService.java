package com.studyboard.flashcard.service;

import com.studyboard.model.Deck;

import java.util.List;

public interface FlashcardService {
    /**
     * Find all decks user created order by the last time they were used for revision
     *
     * @param userId - id of the user who created the deck
     * @return list of all decks order by the last time used attribute
     */
    public List<Deck> getAllDecks(long userId);

    /**
     * Find a single deck by id
     *
     * @param userId - id of the user who created the deck
     * @param deckId of the deck
     * @return the deck with the corresponding id
     */
    public Deck getOneDeck(long userId, long deckId);

    /**
     * Create a single deck
     *
     * @param userId - id of the user who created the deck
     * @param deck   with all the necessary information about a deck
     * @return created deck
     */
    public void createDeck(long userId, Deck deck);

    /**
     * Update a single deck
     *
     * @param userId - id of the user who created the deck
     * @param deck - with the information to be updated
     * @return updated deck
     */
    public Deck updateDeckName(long userId, Deck deck);

}
