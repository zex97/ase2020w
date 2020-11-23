package com.studyboard.flashcard.service;

import com.studyboard.flashcard.exception.DeckDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleFlashcardService {
    @Autowired
    private DeckRepository repository;

    public List<Deck> getAllDecks(){
        return repository.findAll();
    }

    public Deck getOneDeck(Long id) {
        Deck deck = repository.findById(id).orElse(null);
        if(deck == null) {
            throw new DeckDoesNotExist();
        }
        return deck;
    }

    public Deck createDeck(Deck deck) { return repository.save(deck); }
}
