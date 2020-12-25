package com.studyboard.service.implementation;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.exception.DeckDoesNotExist;
import com.studyboard.exception.FlashcardConstraintException;
import com.studyboard.exception.FlashcardDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.FlashcardRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.service.FlashcardService;
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
    public List<Flashcard> getFlashcardsForRevision(long deckId, int size, int version) {
        Deck deck = findDeckById(deckId);
        if (deck.getSize() < size) {
            throw new IllegalArgumentException("Deck size too large!");
        }
        deck.setLastTimeUsed(LocalDateTime.now());
        deckRepository.save(deck);
        if(version == 1) {
            return flashcardRepository.findAllDueToday(deckId, LocalDateTime.now());
        } else {
            return flashcardRepository.findByDeckIdOrderByDueDate(deckId, size);
        }
        /*List<Flashcard> all = getAllFlashcardsOfDeck(deckId);
        List<Flashcard> copy = new ArrayList<>(all);
        List<Flashcard> random = new ArrayList<>();
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < size; i++) {
            random.add(copy.remove(rand.nextInt(copy.size())));
        }
        logger.info("Getting " + size + " flashcards of the deck named " + deck.getName() + " for revision");
        return random;*/
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
        flashcard.setEasiness(2.5);
        flashcard.setCorrectnessStreak(0);
        flashcard.setNextDueDate(LocalDateTime.now());
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
        flashcardRepository.removeAssignment(deckId, flashcardId);
        Deck deck = findDeckById(deckId);
        deck.setSize(deck.getSize() - 1);
        deckRepository.save(deck);
    }

    @Override
    public Flashcard editFlashcard(Flashcard flashcard) {
        Flashcard storedFlashcard = getOneFlashcard(flashcard.getId());
        storedFlashcard.setQuestion(flashcard.getQuestion());
        storedFlashcard.setAnswer(flashcard.getAnswer());
        logger.info("Edited the flashcard with question " + storedFlashcard.getQuestion());
        return flashcardRepository.save(storedFlashcard);
    }

    @Override
    public void rateFlashcard(Flashcard flashcard, int confidence_level) throws FlashcardConstraintException {
        if (confidence_level < 0 || confidence_level > 5) {
            throw new FlashcardConstraintException("Flashcard confidence level must be between 1 and 5!");
        }
        Flashcard storedFlashcard = getOneFlashcard(flashcard.getId());

        //SM-2 Algorithm Calculations
        if(confidence_level >= 3) {
            storedFlashcard.setEasiness(Math.min(1.3, storedFlashcard.getEasiness()-0.8+0.28*confidence_level-0.02*Math.pow(confidence_level, 2)));
            if(storedFlashcard.getCorrectnessStreak() == 0) {
               storedFlashcard.setInterval(1);
            } else if(storedFlashcard.getCorrectnessStreak() == 1) {
                storedFlashcard.setInterval(6);
            } else {
                storedFlashcard.setInterval((int) Math.ceil(storedFlashcard.getInterval()*storedFlashcard.getEasiness()));
            }
            storedFlashcard.setCorrectnessStreak(storedFlashcard.getCorrectnessStreak()+1);
        } else {
            storedFlashcard.setInterval(1);
            storedFlashcard.setCorrectnessStreak(0);
        }
        storedFlashcard.setNextDueDate(LocalDateTime.now().plusDays(storedFlashcard.getInterval()));

        logger.info("Rated the flashcard with question " + storedFlashcard.getQuestion());
        flashcardRepository.save(storedFlashcard);
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



