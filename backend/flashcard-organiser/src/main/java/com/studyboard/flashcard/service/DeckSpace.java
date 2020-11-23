package com.studyboard.flashcard.service;

import com.studyboard.model.Deck;

import java.util.List;

public interface DeckSpace {

    /**
     * Find all decks user created
     *
     * @return list of all decks
     */
    public List<Deck> getAllDecks();

    /**
     * Find a single deck by id
     *
     * @param id of the deck
     * @return the deck with the corresponding id
     */
    public Deck getOneDeck(Long id);

    /**
     * Create a single deck
     *
     * @param deck with all the necessary information about a deck
     * @return created deck
     */
    public Deck createDeck(Deck deck);

    /**TO-DO: connect flashcards to deck*/
}
