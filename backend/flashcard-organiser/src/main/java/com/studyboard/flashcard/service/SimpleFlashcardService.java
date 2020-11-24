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
public class SimpleFlashcardService implements FlashcardService{
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Deck> getAllDecks(long userId){
        User user = findUserById(userId);
        return user.getDecks();
    }
    @Override
    public Deck getOneDeck(long userId, long deckId) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        if(deck == null) {
            throw new DeckDoesNotExist();
        }
        return deck;
    }

    @Override
    public void createDeck(long userId, Deck deck) {
        User user = findUserById(userId);
        user.getDecks().add(deck);
        userRepository.save(user);
    }

    private User findUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }
}
