package com.studyboard.service;

import com.studyboard.exception.DeckDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.FlashcardRepository;
import com.studyboard.service.implementation.SimpleFlashcardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FlashcardServiceTest {
    private static final long USER_ID = 1L;
    private static final String USER_USERNAME = "userTest";
    private static final String USER_PASSWORD = "password1";
    private static final String USER_EMAIL = "demo1@email.com";
    private static final Integer USER_LOGIN_ATTEMPTS = 0;
    private static final Boolean USER_ENABLED = true;
    private static final String USER_ROLE = "ADMIN";

    private static final Long DECK_ID = 1L;
    private static final String DECK_NAME = "Test";
    private static final Integer DECK_SIZE = 0;
    private static final LocalDateTime DECK_LAST_TIME_USED = LocalDateTime.of(2020, 12, 02, 15, 17);
    private static final LocalDate DECK_CREATION_DATE = LocalDate.of(2020, 12, 2);

    private static final Long FLASHCARD_ID = 1L;
    private static final String FLASHCARD_QUESTION = "Question";
    private static final String FLASHCARD_ANSWER = "Answer";
    private static final int CONFIDENCE_LEVEL = 0;


    @Mock
    private DeckRepository deckRepository;

    @Mock
    private FlashcardRepository flashcardRepository;

    @InjectMocks
    private SimpleFlashcardService flashcardService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllDecksFromAUser() {

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
        user.setRole(USER_ROLE);
        user.setEnabled(USER_ENABLED);

        Deck deck = new Deck();
        deck.setId(DECK_ID);
        deck.setName(DECK_NAME);
        deck.setSize(DECK_SIZE);
        deck.setCreationDate(DECK_CREATION_DATE);
        deck.setLastTimeUsed(DECK_LAST_TIME_USED);
        deck.setUser(user);

        Mockito.when(deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME)).thenReturn(Arrays.asList(deck));

        List<Deck> response = flashcardService.getAllDecks(USER_USERNAME);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(java.util.Optional.of(DECK_ID), java.util.Optional.of(response.get(0).getId()));
        Assertions.assertEquals(DECK_NAME, response.get(0).getName());
        Assertions.assertEquals(DECK_SIZE, response.get(0).getSize());
        Assertions.assertEquals(DECK_CREATION_DATE, response.get(0).getCreationDate());
        Assertions.assertEquals(DECK_LAST_TIME_USED, response.get(0).getLastTimeUsed());
    }

    @Test
    public void searchForDeckThatDoesNotExist_ThrowsException() {
        Assertions.assertThrows(DeckDoesNotExist.class, () -> {
            flashcardService.findDeckById(DECK_ID);
        });
    }

    @Test
    public void addingAndAssigningFlashcardIncreasesDeckSize() {
        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
        user.setRole(USER_ROLE);
        user.setEnabled(USER_ENABLED);

        Deck deck = new Deck();
        deck.setId(DECK_ID);
        deck.setName(DECK_NAME);
        deck.setSize(DECK_SIZE);
        deck.setCreationDate(DECK_CREATION_DATE);
        deck.setLastTimeUsed(DECK_LAST_TIME_USED);
        deck.setUser(user);

        List<Deck> decks = new ArrayList<>();
        decks.add(deck);

        Mockito.when(deckRepository.findDeckById(DECK_ID)).thenReturn(deck);

        Deck response = flashcardService.findDeckById(DECK_ID);

        Assertions.assertEquals(0, response.getSize());

        Flashcard flashcard = new Flashcard();
        flashcard.setId(FLASHCARD_ID);
        flashcard.setQuestion(FLASHCARD_QUESTION);
        flashcard.setAnswer(FLASHCARD_ANSWER);
        flashcard.setConfidenceLevel(CONFIDENCE_LEVEL);
        flashcard.setDecks(decks);
        flashcardService.createFlashcard(flashcard);
        flashcardService.assignFlashcard(flashcard.getId(), response.getId() + "-");

        response = flashcardService.findDeckById(DECK_ID);

        Assertions.assertEquals(1, response.getSize());
    }
}
