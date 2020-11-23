package com.studyboard.flashcard.service;

import com.studyboard.model.Deck;

import java.util.List;

public interface DeckSpace {

    public List<Deck> getAllDecks();

    public Deck getOneDeck(Long id);

    public Deck createDeck(Deck deck);
}
