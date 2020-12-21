package com.studyboard.flashcard.service;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.flashcard.exception.DeckDoesNotExist;
import com.studyboard.flashcard.exception.FlashcardConstraintException;
import com.studyboard.flashcard.exception.FlashcardDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.FlashcardRepository;
import com.studyboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Service used to manage decks and flashcards. Performs decks and flashcards creation, getting, edit and deletion */
@Service
public class SimpleFlashcardService implements FlashcardService {

    private final Logger logger = LoggerFactory.getLogger(FlashcardService.class);

    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlashcardRepository flashcardRepository;

    @Override
    public List<Deck> getAllDecks(String username) {
        logger.info("Getting all decks belonging to the user with username " + username);
        return deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(username);
    }

    @Override
    public void createDeck(Deck deck) {
        deckRepository.save(deck);
        logger.info("Created new deck with name "
                + deck.getName() +
                " for user with username "
                + deck.getUser().getUsername());
    }

    @Override
    public Deck updateDeckName(Deck deck) {
        Deck storedDeck = findDeckById(deck.getId());
        logger.info("Changed the deck name: from "
                + storedDeck.getName() + " to: "
                + deck.getName());
        storedDeck.setName(deck.getName());
        return deckRepository.save(storedDeck);
    }

    @Override
    public List<Flashcard> getAllFlashcardsOfDeck(long deckId) {
        Deck deck = findDeckById(deckId);
        logger.info("Getting all flashcards belonging to the deck with name " + deck.getName());
        return flashcardRepository.findByDeckId(deckId);
    }

    @Override
    public List<Flashcard> getFlashcardsForRevision(long deckId, int size) {
        Deck deck = findDeckById(deckId);
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
        logger.info("Getting " + size + " flashcards of the deck named " + deck.getName() + " for revision");
        return random;
    }

    @Override
    public Flashcard getOneFlashcard(long flashcardId) {
        Flashcard flashcard = flashcardRepository.findFlashcardById(flashcardId);
        if (flashcard == null) {
            throw new FlashcardDoesNotExist();
        }
        logger.info("Getting flashcard with question " + flashcard.getQuestion());
        return flashcard;
    }

    @Override
    public Flashcard createFlashcard(Flashcard flashcard) {
        flashcard.setConfidenceLevel(0);
        logger.info("Created new flashcard with question " + flashcard.getQuestion());
        return flashcardRepository.save(flashcard);
    }

    @Override
    public void assignFlashcard(long flashcardId, String decks) {
        String[] deckIds = decks.split("-");
        for (int i = 0; i < deckIds.length; i++) {
            if (!deckIds[i].equals("")) {
                long id = Long.parseLong(deckIds[i]);
                flashcardRepository.assignFlashcard(id, flashcardId);
                Deck deck = findDeckById(id);
                deck.setSize(deck.getSize() + 1);
                deckRepository.save(deck);
            }
        }
    }


    @Override
    public void deleteDeck(long deckId) {
        Deck deck = findDeckById(deckId);
        deckRepository.deleteById(deckId);
        logger.info("Delete deck with name " + deck.getName());
    }

    @Override
    public void deleteFlashcard(long deckId, long flashcardId) {
        Deck deck = findDeckById(deckId);
        deck.setSize(deck.getSize() - 1);
        Flashcard flashcard = findFlashcardById(flashcardId);
        if (flashcard.getDecks().size() == 1) {
            flashcardRepository.deleteById(flashcardId);
        } else {
            List<Deck> decks = flashcard.getDecks();
            decks.remove(deck);
            flashcard.setDecks(decks);
            flashcardRepository.save(flashcard);
        }
    }

    @Override
    public Flashcard editFlashcard(Flashcard flashcard) throws FlashcardConstraintException {
        Flashcard storedFlashcard = getOneFlashcard(flashcard.getId());
        try {
            storedFlashcard.setQuestion(flashcard.getQuestion());
            storedFlashcard.setAnswer(flashcard.getAnswer());
            storedFlashcard.setConfidenceLevel(flashcard.getConfidenceLevel());
            logger.info("Edited or rated the flashcard with question " + storedFlashcard.getQuestion());
            return flashcardRepository.save(storedFlashcard);
        } catch (ConstraintViolationException e) {
            throw new FlashcardConstraintException("Flashcard confidence level must be between 1 and 5!");
        }
    }

    @Override
    public Deck findDeckById(Long deckId) {
        Deck deck = deckRepository.findDeckById(deckId);
        if (deck == null) {
            logger.warn("Deck does not exist");
            throw new DeckDoesNotExist();
        }
        return deck;
    }

    public Flashcard findFlashcardById(Long flashcardId) {
        Flashcard flashcard = flashcardRepository.findFlashcardById(flashcardId);
        if (flashcard == null) {
            logger.warn("Flashcard does not exist");
            //throw new DeckDoesNotExist();
        }
        return flashcard;
    }

    private User findUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            logger.warn("User does not exist");
            throw new UserDoesNotExist();
        }
        return user;
    }
}



