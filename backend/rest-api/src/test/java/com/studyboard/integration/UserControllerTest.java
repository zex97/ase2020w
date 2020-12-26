package com.studyboard.integration;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends BaseIntegrationTest {

    private static final String USER_ENDPOINT = "/api/user";
    private static final String USER_ID_PATH = "/{userID}";
    private static final String USER_USERNAME_PATH = "/username{username}";

    private static final User TEST_USER_1 = new User("testUsername1", "testPassword1", "user1@email.com", 2, "USER", true);
    private static final User TEST_USER_2 = new User("testUsername2", "testPassword2", "user2@email.com", 0, "USER", true);
    private static final String TEST_UPDATED_PASSWORD = "updatedPassword";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "sb_user", "user_roles");
    }

    @Test
    public void findUserByIdWhichDoesNotExistThrowsException() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(USER_ENDPOINT + USER_ID_PATH, 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof UserDoesNotExist));
    }

    @Test
    public void getUserByUsernameWhichDoesNotExistThrowsException() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(USER_ENDPOINT + USER_USERNAME_PATH, "user1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof UserDoesNotExist));
    }

    @Test
    public void getUserByUsernameSuccessfully() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
//                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(USER_ENDPOINT + USER_USERNAME_PATH, TEST_USER_1.getUsername()).accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(TEST_USER_1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(TEST_USER_1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.loginAttempts").value(TEST_USER_1.getLoginAttempts()));
    }

    @Test
    public void createOneUserGetAllAndTestPasswordEncoderSuccessfully() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
//                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultActions =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(TEST_USER_1.getUsername()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(TEST_USER_1.getEmail()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER_1.getLoginAttempts()));

        String responseString = resultActions.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);
        Assertions.assertTrue(passwordEncoder.matches(user.getPassword(), responseArray[0].getPassword()));
    }

    @Test
    public void createTwoUsersWithSameUsernameThrowsException() throws Exception {
        User user1 = new User(TEST_USER_1);
        User user2 = new User(TEST_USER_2);
        user2.setUsername(user1.getUsername());
        String requestJson1 = convertObjectToStringForJson(user1);
        String requestJson2 = convertObjectToStringForJson(user2);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson1))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson2))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof UniqueConstraintException))
                .andExpect(mvcResult -> Assertions.assertEquals("User with the same username or email already exists.", mvcResult.getResolvedException().getMessage()));
    }

    @Test
    public void editUserPasswordSuccessfully() throws Exception {
        User user = new User(TEST_USER_1);
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
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(TEST_USER_1.getUsername()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(TEST_USER_1.getEmail()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER_1.getLoginAttempts()));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        User userUpdatedPassword = new User(TEST_USER_1);
        userUpdatedPassword.setId(responseArray[0].getId());
        userUpdatedPassword.setPassword(TEST_UPDATED_PASSWORD);
        String requestJsonUpdated = convertObjectToStringForJson(userUpdatedPassword);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJsonUpdated))
//                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultActionsUpdated =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(TEST_USER_1.getUsername()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(TEST_USER_1.getEmail()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER_1.getLoginAttempts()));

        String responseStringUpdated = resultActionsUpdated.andReturn().getResponse().getContentAsString();
        User[] responseArrayUpdated = mapper.readValue(responseStringUpdated, User[].class);
        Assertions.assertTrue(passwordEncoder.matches(userUpdatedPassword.getPassword(), responseArrayUpdated[0].getPassword()));
        Assertions.assertEquals(responseArray[0].getId(), responseArrayUpdated[0].getId());

    }

    @Test
    public void resetLoginAttemptsSuccessfully() throws Exception {
        User user = new User(TEST_USER_1);
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
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(TEST_USER_1.getLoginAttempts()));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.put(USER_ENDPOINT + USER_ID_PATH, responseArray[0].getId()))
//                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultActionsUpdated =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
//                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(responseArray[0].getId()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$[0].loginAttempts").value(0));

    }
}
