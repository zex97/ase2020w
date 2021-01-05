package com.studyboard.integration;


import com.studyboard.dto.SpaceDTO;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserSpaceControllerTest extends BaseIntegrationTest {

    private static final String SPACE_ENDPOINT = "/api/space";
    private static final String USER_ENDPOINT = "/api/user";

    private static final User TEST_USER = new User("testUsername", "testPassword", "user@email.com", 2, "USER", true);
    private static Space TEST_SPACE_1, TEST_SPACE_2;

    @BeforeEach
    public void setUp() throws Exception {
        User user = new User(TEST_USER);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER.getLoginAttempts()));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);
        TEST_SPACE_1 =  new Space("test1", responseArray[0]);
        TEST_SPACE_2 =  new Space("test2", responseArray[0]);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "space");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "sb_user", "user_roles");
    }


    @Test
    public void searchForAllSpacesReturnsCorrectResults() throws Exception {
        SpaceDTO space1 = SpaceDTO.of(TEST_SPACE_1);
        String requestJson1 = convertObjectToStringForJson(space1);
        SpaceDTO space2 = SpaceDTO.of(TEST_SPACE_2);
        String requestJson2 = convertObjectToStringForJson(space2);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        ResultActions resultActionsSpace =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/testUsername").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        String responseString = resultActionsSpace.andReturn().getResponse().getContentAsString();
        SpaceDTO[] responseArray = mapper.readValue(responseString, SpaceDTO[].class);

        Assertions.assertEquals(TEST_SPACE_1.getName(), responseArray[0].getName());
        Assertions.assertEquals(TEST_SPACE_2.getName(), responseArray[1].getName());
    }

    @Test
    public void searchForSpacesByNameReturnsCorrectResults() throws Exception {
        SpaceDTO space1 = SpaceDTO.of(TEST_SPACE_1);
        String requestJson1 = convertObjectToStringForJson(space1);
        SpaceDTO space2 = SpaceDTO.of(TEST_SPACE_2);
        String requestJson2 = convertObjectToStringForJson(space2);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        ResultActions resultActionsSpace =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/search/testUsername/test").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

        String responseString = resultActionsSpace.andReturn().getResponse().getContentAsString();
        SpaceDTO[] responseArray = mapper.readValue(responseString, SpaceDTO[].class);

        Assertions.assertEquals(TEST_SPACE_1.getName(), responseArray[0].getName());
        Assertions.assertEquals(TEST_SPACE_2.getName(), responseArray[1].getName());
    }


    @Test
    public void createSpaceSuccessfully() throws Exception {
        SpaceDTO space1 = SpaceDTO.of(TEST_SPACE_1);
        String requestJson1 = convertObjectToStringForJson(space1);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson1))
                .andExpect(status().isOk());

        ResultActions resultActionsSpace =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/search/testUsername/test1").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsSpace.andReturn().getResponse().getContentAsString();
        SpaceDTO[] responseArray = mapper.readValue(responseString, SpaceDTO[].class);

        Assertions.assertEquals(TEST_SPACE_1.getName(), responseArray[0].getName());
    }


    @Test
    public void findSpaceByNameWhichDoesNotExistThrowsException() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/search/testUsername/testX").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

    }

    @Test
    public void createAndEditSpaceNameSuccessfully() throws Exception {
        SpaceDTO space = SpaceDTO.of(TEST_SPACE_1);
        String requestJson = convertObjectToStringForJson(space);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsSpace =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/search/testUsername/test1").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(TEST_SPACE_1.getName()));

        String responseString = resultActionsSpace.andReturn().getResponse().getContentAsString();
        SpaceDTO[] responseArray = mapper.readValue(responseString, SpaceDTO[].class);

        SpaceDTO spaceUpdatedName = SpaceDTO.of(TEST_SPACE_1);
        spaceUpdatedName.setId(responseArray[0].getId());
        spaceUpdatedName.setName("nameUpdated");
        String requestJsonUpdated = convertObjectToStringForJson(spaceUpdatedName);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(SPACE_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJsonUpdated))
                .andExpect(status().isOk());
        ResultActions resultActionsUpdated =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(SPACE_ENDPOINT + "/search/testUsername/nameUpdated").accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(spaceUpdatedName.getName()));

        String responseStringUpdated = resultActionsUpdated.andReturn().getResponse().getContentAsString();
        SpaceDTO[] responseArrayUpdated = mapper.readValue(responseStringUpdated, SpaceDTO[].class);
        Assertions.assertEquals(spaceUpdatedName.getName(), responseArrayUpdated[0].getName());
        Assertions.assertEquals(responseArray[0].getId(), responseArrayUpdated[0].getId());
    }

}
