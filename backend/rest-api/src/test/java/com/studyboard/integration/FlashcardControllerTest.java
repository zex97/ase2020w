package com.studyboard.integration;

import com.studyboard.dto.DeckDTO;
import com.studyboard.dto.FlashcardDTO;
import com.studyboard.exception.DeckDoesNotExist;
import com.studyboard.exception.FlashcardDoesNotExist;
import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import com.studyboard.model.User;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FlashcardControllerTest extends BaseIntegrationTest {

    private static final String FLASHCARD_ENDPOINT = "/api/flashcards";
    private static final String DECK_ID_PATH = "/deck{deckID}";
    private static final String FLASHCARD_ID_PATH = "/flashcard{flashcardID}";
    private static final String USER_ENDPOINT = "/api/user";

    private static final User TEST_USER = new User("testUsername", "testPassword", "user@email.com", 2, "USER", true);
    private static Deck TEST_DECK, TEST_DECK_2;
    private static Flashcard TEST_FLASHCARD, TEST_FLASHCARD_2;

    @BeforeEach
    public void setUp() throws Exception {
        User user = new User(TEST_USER);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
//                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER.getLoginAttempts()));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);
        TEST_DECK = new Deck("testName", 0, LocalDate.of(2020, 12, 10), LocalDateTime.of(2020, 12, 10, 23, 55, 55), responseArray[0]);
        TEST_DECK_2 =  new Deck("test_2", 0, LocalDate.of(2020, 12, 29), LocalDateTime.of(2020, 12, 29, 23, 55, 55), responseArray[0]);
        TEST_FLASHCARD = new Flashcard("question", "answer");
        TEST_FLASHCARD_2 = new Flashcard("question2", "answer2");
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "flashcards_assignment", "flashcard");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "deck");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "sb_user", "user_roles");
    }


    @Test
    public void findDeckByIdWhichDoesNotExistThrowsException() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + DECK_ID_PATH, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof DeckDoesNotExist));

    }

    @Test
    public void findFlashcardByIdWhichDoesNotExistThrowsException() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/1" + FLASHCARD_ID_PATH, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof FlashcardDoesNotExist));

    }

    @Test
    public void createAndEditDeckNameSuccessfully() throws Exception {
        DeckDTO deck = DeckDTO.of(TEST_DECK);
        String requestJson = convertObjectToStringForJson(deck);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
//                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultActionsDeck =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/testUsername").accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(TEST_DECK.getName()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].size").value(TEST_DECK.getSize()));

        String responseString = resultActionsDeck.andReturn().getResponse().getContentAsString();
        DeckDTO[] responseArray = mapper.readValue(responseString, DeckDTO[].class);

        DeckDTO deckUpdatedName = DeckDTO.of(TEST_DECK);
        deckUpdatedName.setId(responseArray[0].getId());
        deckUpdatedName.setName("nameUpdated");
        String requestJsonUpdated = convertObjectToStringForJson(deckUpdatedName);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJsonUpdated))
//                .andDo(print())
                .andExpect(status().isOk());
        ResultActions resultActionsUpdated =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/testUsername").accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(deckUpdatedName.getName()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].size").value(TEST_DECK.getSize()));

        String responseStringUpdated = resultActionsUpdated.andReturn().getResponse().getContentAsString();
        DeckDTO[] responseArrayUpdated = mapper.readValue(responseStringUpdated, DeckDTO[].class);
        Assertions.assertEquals(deckUpdatedName.getName(), responseArrayUpdated[0].getName());
        Assertions.assertEquals(responseArray[0].getId(), responseArrayUpdated[0].getId());
    }

    @Test
    public void getOneDeckReturnsCorrectResult() throws Exception{
        DeckDTO deck1 = DeckDTO.of(TEST_DECK);
        String requestJson1 = convertObjectToStringForJson(deck1);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        ResultActions resultActionsDeck =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + DECK_ID_PATH,2).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        String responseString = resultActionsDeck.andReturn().getResponse().getContentAsString();
        DeckDTO response = mapper.readValue(responseString, DeckDTO.class);

        Assertions.assertEquals(TEST_DECK.getName(), response.getName());
    }

    @Test
    public void deleteOneDeckReturnsCorrectResult() throws Exception{
        DeckDTO deck1 = DeckDTO.of(TEST_DECK);
        String requestJson1 = convertObjectToStringForJson(deck1);
        DeckDTO deck2 = DeckDTO.of(TEST_DECK_2);
        String requestJson2 = convertObjectToStringForJson(deck2);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(FLASHCARD_ENDPOINT + "/{deckID}", 4).contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        ResultActions resultActionsDeck =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/testUsername").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        String responseString = resultActionsDeck.andReturn().getResponse().getContentAsString();
        DeckDTO[] responseArray = mapper.readValue(responseString, DeckDTO[].class);

        Assertions.assertEquals(TEST_DECK.getName(), responseArray[0].getName());
    }

    @Test
    public void searchForDecksByNameReturnsCorrectResults() throws Exception{
        DeckDTO deck1 = DeckDTO.of(TEST_DECK);
        String requestJson1 = convertObjectToStringForJson(deck1);
        DeckDTO deck2 = DeckDTO.of(TEST_DECK_2);
        String requestJson2 = convertObjectToStringForJson(deck2);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        ResultActions resultActionsDeck =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/testUsername/test").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        String responseString = resultActionsDeck.andReturn().getResponse().getContentAsString();
        DeckDTO[] responseArray = mapper.readValue(responseString, DeckDTO[].class);

        Assertions.assertEquals(TEST_DECK.getName(), responseArray[0].getName());
        Assertions.assertEquals(TEST_DECK_2.getName(), responseArray[1].getName());
    }

    @Test
    public void createAndEditFlashcardSuccessfully() throws Exception {
        DeckDTO deck = DeckDTO.of(TEST_DECK);
        String requestJson = convertObjectToStringForJson(deck);
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        TEST_DECK.setId(5L);
        TEST_FLASHCARD.setDecks(List.of(TEST_DECK));
        TEST_FLASHCARD.setDocumentReferences(new ArrayList<>());

        FlashcardDTO flashcardDTO = FlashcardDTO.FlashcardDTOFromFlashcard(TEST_FLASHCARD);
        String requestJson1 = convertObjectToStringForJson(flashcardDTO);
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT + "/flashcard").contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        ResultActions resultActionsFlash =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/{deckID}/flashcards", 5).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].question").value(TEST_FLASHCARD.getQuestion()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].answer").value(TEST_FLASHCARD.getAnswer()));

        String responseString = resultActionsFlash.andReturn().getResponse().getContentAsString();
        FlashcardDTO[] responseArray = mapper.readValue(responseString, FlashcardDTO[].class);

        FlashcardDTO flashcardDTO1 = FlashcardDTO.FlashcardDTOFromFlashcard(TEST_FLASHCARD);
        flashcardDTO1.setId(responseArray[0].getId());
        flashcardDTO1.setQuestion("questionNew");
        flashcardDTO1.setAnswer("answerNew");
        String requestJsonUpdated = convertObjectToStringForJson(flashcardDTO1);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(FLASHCARD_ENDPOINT + FLASHCARD_ID_PATH, 6).contentType(MediaType.APPLICATION_JSON).content(requestJsonUpdated))
                .andExpect(status().isOk());

        ResultActions resultActionsUpdated =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/{deckID}/flashcards", 5).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].question").value(flashcardDTO1.getQuestion()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].answer").value(flashcardDTO1.getAnswer()));

        String responseStringUpdated = resultActionsUpdated.andReturn().getResponse().getContentAsString();
        FlashcardDTO[] responseArrayUpdated = mapper.readValue(responseStringUpdated, FlashcardDTO[].class);
        Assertions.assertEquals(flashcardDTO1.getQuestion(), responseArrayUpdated[0].getQuestion());
        Assertions.assertEquals(flashcardDTO1.getAnswer(), responseArrayUpdated[0].getAnswer());
        Assertions.assertEquals(responseArray[0].getId(), responseArrayUpdated[0].getId());
    }

    @Test
    public void getOneFlashcardReturnsCorrectResult() throws Exception {
        DeckDTO deck = DeckDTO.of(TEST_DECK);
        String requestJson = convertObjectToStringForJson(deck);
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        TEST_DECK.setId(9L);
        TEST_FLASHCARD.setDecks(List.of(TEST_DECK));
        TEST_FLASHCARD.setDocumentReferences(new ArrayList<>());

        FlashcardDTO flashcardDTO = FlashcardDTO.FlashcardDTOFromFlashcard(TEST_FLASHCARD);
        String requestJson1 = convertObjectToStringForJson(flashcardDTO);
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(FLASHCARD_ENDPOINT + "/flashcard").contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        ResultActions resultActionsFlash =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(FLASHCARD_ENDPOINT + "/{deckID}" + FLASHCARD_ID_PATH, 9, 10).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.question").value(TEST_FLASHCARD.getQuestion()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.answer").value(TEST_FLASHCARD.getAnswer()));

        String responseString = resultActionsFlash.andReturn().getResponse().getContentAsString();
        FlashcardDTO response = mapper.readValue(responseString, FlashcardDTO.class);

        Assertions.assertEquals(TEST_FLASHCARD.getQuestion(), response.getQuestion());
        Assertions.assertEquals(TEST_FLASHCARD.getAnswer(), response.getAnswer());
    }
}
