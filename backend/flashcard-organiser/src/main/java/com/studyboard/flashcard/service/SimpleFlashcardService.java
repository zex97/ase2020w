package com.studyboard.flashcard.service;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.flashcard.exception.DeckDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleFlashcardService implements FlashcardService {

    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Deck> getAllDecks(long userId) {
        return deckRepository.findByUserIdOrderByLastTimeUsedDesc(userId);
    }

    @Override
    public Deck getOneDeck(long userId, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        User user = findUserById(userId);
        if (deck == null) {
            throw new DeckDoesNotExist();
        }
        if (deck.getUser() != user) {
            //create a Not Allowed exception
        }
        return deck;
    }

    @Override
    public void createDeck(long userId, Deck deck) {
        deckRepository.save(deck);
    }

    @Override
    public Deck updateDeckName(long userId, Deck deck) {
        Deck storedDeck = getOneDeck(userId, deck.getId());
        storedDeck.setName(deck.getName());
        return deckRepository.save(storedDeck);
    }

    private User findUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }
}
