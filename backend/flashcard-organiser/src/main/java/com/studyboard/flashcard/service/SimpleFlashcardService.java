package com.studyboard.flashcard.service;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.flashcard.exception.DeckDoesNotExist;
import com.studyboard.flashcard.exception.FlashcardDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.FlashcardRepository;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleFlashcardService implements FlashcardService {

    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlashcardRepository flashcardRepository;

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

    @Override
    public List<Flashcard> getAllFlashcardsOfDeck(long deckId) {
        return flashcardRepository.findByDeckId(deckId);
    }

    @Override
    public List<Flashcard> getFlashcardsForRevision(long userId, long deckId, int size) {
        Deck deck = getOneDeck(userId, deckId);
        if (deck.getSize() < size) {
            throw new IllegalArgumentException("Deck size too large!");
        }
        deck.setLastTimeUsed(LocalDateTime.now());
        deckRepository.save(deck);
        //get random flashcards - later: implement an algorithm
        List<Flashcard> all = getAllFlashcardsOfDeck(deckId);
        List<Flashcard> copy = new ArrayList<>(all);
        List<Flashcard> random = new ArrayList<>();
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < size; i++) {
            random.add(copy.remove(rand.nextInt(copy.size())));
        }
        return random;
    }

    @Override
    public Flashcard getOneFlashcard(long deckId, long flashcardId) {
        Flashcard flashcard = flashcardRepository.findFlashcardById(flashcardId);
        Deck deck = findDeckById(deckId);
        if (flashcard == null) {
            throw new FlashcardDoesNotExist();
        }
        if (flashcard.getDeck() != deck) {
            //create a Not Allowed exception
        }
        return flashcard;
    }

    @Override
    public void createFlashcard(long deckId, Flashcard flashcard) {
        flashcardRepository.save(flashcard);
        Deck deck = findDeckById(deckId);
        deck.setSize(deck.getSize() + 1);
        deckRepository.save(deck);
    }

    @Override
    public void deleteDeck(long userId, long deckId) {
        User user = findUserById(userId);
        user.getDecks().removeIf(d -> d.getId() == deckId);
        userRepository.save(user);
    }

    @Override
    public void deleteFlashcard(long deckId, long flashcardId) {
        Deck deck = findDeckById(deckId);
        deck.getFlashcards().removeIf(f -> f.getId() == flashcardId);
        deck.setSize(deck.getSize() - 1);
        deckRepository.save(deck);
    }


    private User findUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }


    private Deck findDeckById(long deckId) {
        Deck deck = deckRepository.findById(deckId).orElse(null);
        if (deck == null) {
            throw new DeckDoesNotExist();
        }
        return deck;
    }
}
