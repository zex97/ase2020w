package com.studyboard;

import com.studyboard.model.Deck;
import com.studyboard.model.User;
import com.studyboard.repository.DeckRepository;
import com.studyboard.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@ExtendWith(SpringExtension.class)
//@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@SpringBootTest(classes = DeckRepository.class)
@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes= StudyboardApplication.class)
@DataJpaTest
public class DeckRepositoryTest {

  private static final long USER_ID = 1L;
  private static final String USER_USERNAME = "userTest";
  private static final String USER_PASSWORD = "password1";
  private static final String USER_EMAIL = "demo1@email.com";
  private static final Integer USER_LOGIN_ATTEMPTS = 0;
  private static final Boolean USER_ENABLED = true;
  private static final String USER_ROLE = "ADMIN";

  private static final Long DECK_ID = 2L;
  private static final String DECK_NAME = "Test";
  private static final Integer DECK_SIZE = 0;
  private static final LocalDateTime DECK_LAST_TIME_USED = LocalDateTime.of(2020, 12, 2, 15, 17);
  private static final LocalDate DECK_CREATION_DATE = LocalDate.of(2020, 12, 2);

  private static final Long DECK_ID_2 = 3L;
  private static final String DECK_NAME_2 = "Test2";
  private static final Integer DECK_SIZE_2 = 0;
  private static final LocalDateTime DECK_LAST_TIME_USED_2 = LocalDateTime.of(2020, 12, 3, 15, 17);
  private static final LocalDate DECK_CREATION_DATE_2 = LocalDate.of(2020, 12, 3);

  @Autowired private DeckRepository deckRepository;

  @Autowired private UserRepository userRepository;

  @Test
  public void repositorySavesDeckCorrectly() {
    Assertions.assertEquals(
        0, deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME).size());

    User user = new User();
    user.setId(USER_ID);
    user.setUsername(USER_USERNAME);
    user.setPassword(USER_PASSWORD);
    user.setEmail(USER_EMAIL);
    user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
    user.setRole(USER_ROLE);
    user.setEnabled(USER_ENABLED);
    userRepository.save(user);

    Deck deck = new Deck();
    deck.setName(DECK_NAME);
    deck.setSize(DECK_SIZE);
    deck.setCreationDate(DECK_CREATION_DATE);
    deck.setLastTimeUsed(DECK_LAST_TIME_USED);
    deck.setUser(user);
    deckRepository.save(deck);

    List<Deck> decks = deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME);
    Assertions.assertEquals(1, decks.size());

    Deck storedDeck = decks.get(0);
    Assertions.assertEquals(DECK_NAME, storedDeck.getName());
    Assertions.assertEquals(DECK_SIZE, storedDeck.getSize());
    Assertions.assertEquals(DECK_CREATION_DATE, storedDeck.getCreationDate());
    Assertions.assertEquals(DECK_LAST_TIME_USED, storedDeck.getLastTimeUsed());
  }

  @Test
  public void repositoryReturnsDecksOrderedDescendingByCreationDate() {
    Assertions.assertEquals(
        0, deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME).size());

    User user = new User();
    user.setId(USER_ID);
    user.setUsername(USER_USERNAME);
    user.setPassword(USER_PASSWORD);
    user.setEmail(USER_EMAIL);
    user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
    user.setRole(USER_ROLE);
    user.setEnabled(USER_ENABLED);
    userRepository.save(user);

    Deck deck = new Deck();
    deck.setId(DECK_ID);
    deck.setName(DECK_NAME);
    deck.setSize(DECK_SIZE);
    deck.setCreationDate(DECK_CREATION_DATE);
    deck.setLastTimeUsed(DECK_LAST_TIME_USED);
    deck.setUser(user);
    deckRepository.save(deck);
    deck.setId(DECK_ID_2);
    deck.setName(DECK_NAME_2);
    deck.setSize(DECK_SIZE_2);
    deck.setCreationDate(DECK_CREATION_DATE_2);
    deck.setLastTimeUsed(DECK_LAST_TIME_USED_2);
    deckRepository.save(deck);

    Assertions.assertEquals(
        2, deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME).size());

    List<Deck> decks = deckRepository.findByUserUsernameOrderByLastTimeUsedDesc(USER_USERNAME);
    Assertions.assertEquals(DECK_NAME_2, decks.get(0).getName());
    Assertions.assertEquals(DECK_NAME, decks.get(1).getName());
  }
}
