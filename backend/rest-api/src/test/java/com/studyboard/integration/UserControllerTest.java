package com.studyboard.integration;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.PasswordResetToken;
import com.studyboard.model.User;
import com.studyboard.repository.ResetTokenRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.mail.Message;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends BaseIntegrationTest {

    private static final String USER_ENDPOINT = "/api/user";
    private static final String USER_ID_PATH = "/{userID}";
    private static final String USER_USERNAME_PATH = "/username{username}";
    private static final String USER_VERIFY_EMAIL_PATH = "/reset/{email}";
    private static final String USER_VERIFY_TOKEN_PATH = "/reset/token/{token}";
    private static final String USER_CHANGE_PASSW_TOKEN_PATH = "/reset/change/{token}";

    private static final User TEST_USER_1 = new User("testUsername1", "testPassword1", "user1@email.com", 2, "USER", true);
    private static final User TEST_USER_2 = new User("testUsername2", "testPassword2", "user2@email.com", 0, "USER", true);
    private static final String TEST_UPDATED_PASSWORD = "updatedPassword";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    private GreenMail testSmtp;

    private final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @BeforeAll
    public void testSmtpInit(){
        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.setUser("studyboard.example@gmail.com", "studyboardpassword");
        testSmtp.setUser(TEST_USER_1.getEmail(),TEST_USER_1.getPassword());
        testSmtp.start();

        mailSender.setPort(3025);
        mailSender.setHost("localhost");
    }

    @AfterAll
    public void cleanup(){
        testSmtp.stop();
    }

    @AfterEach
    void tearDown() {
        try {
            testSmtp.purgeEmailFromAllMailboxes();
        } catch (FolderException e) {
            logger.error("Error deleting test emails: " + e);
        }
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "password_reset_token", "sb_user", "user_roles");
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

    @Test
    public void verifyEmailAndSendRecoveryToken() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_EMAIL_PATH, TEST_USER_1.getEmail()))
                .andExpect(status().isOk());

        List<PasswordResetToken> token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());
        responseArray[0].setEnabled(true);
        Assertions.assertEquals(responseArray[0], token.get(0).getUser());

        Message[] messages = testSmtp.getReceivedMessages();
        Assertions.assertEquals(1, messages.length);
        Assertions.assertEquals("studyboard.example@gmail.com", messages[0].getFrom()[0].toString());
        Assertions.assertEquals("Reset password", messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("\r\n", "");
        String link = "http://localhost:4200/changePassword?token=" + token.get(0).getToken();
        String expectedBody = "Hello " + user.getUsername() + "! Click on the link to reset you password. " + link;
        Assertions.assertEquals(expectedBody, body);
    }

    @Test
    public void verifyResetToken() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_EMAIL_PATH, TEST_USER_1.getEmail()))
                .andExpect(status().isOk());

        List<PasswordResetToken> token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_TOKEN_PATH, token.get(0).getToken()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token.get(0).getToken()));


    }

    @Test
    public void verifyExpiredToken() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_EMAIL_PATH, TEST_USER_1.getEmail()))
                .andExpect(status().isOk());

        List<PasswordResetToken> token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());

        // 25 hours later
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.HOUR, -25);
        token.get(0).setExpires(expires.getTimeInMillis());
        resetTokenRepository.save(token.get(0));

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_TOKEN_PATH, token.get(0).getToken()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("invalid"));


    }

    @Test
    public void changePasswordWithTokenSuccessfully() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_EMAIL_PATH, TEST_USER_1.getEmail()))
                .andExpect(status().isOk());

        User userWithNewPassword = new User(TEST_USER_1);
        userWithNewPassword.setPassword(TEST_UPDATED_PASSWORD);
        String requestJson2 = convertObjectToStringForJson(userWithNewPassword);

        List<PasswordResetToken> token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_CHANGE_PASSW_TOKEN_PATH, token.get(0).getToken())
                .contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isOk());

        //token consumed
        token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(0, token.size());

        ResultActions resultActionsAfterPasswordChange =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        responseString = resultActionsAfterPasswordChange.andReturn().getResponse().getContentAsString();
        responseArray = mapper.readValue(responseString, User[].class);

        Assertions.assertTrue(passwordEncoder.matches(TEST_UPDATED_PASSWORD, responseArray[0].getPassword()));
    }

    @Test
    public void changePasswordWithInvalidTokenReturnsBadRequest() throws Exception {
        User user = new User(TEST_USER_1);
        String requestJson = convertObjectToStringForJson(user);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        ResultActions resultActionsUser =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
        User[] responseArray = mapper.readValue(responseString, User[].class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_VERIFY_EMAIL_PATH, TEST_USER_1.getEmail()))
                .andExpect(status().isOk());

        User userWithNewPassword = new User(TEST_USER_1);
        userWithNewPassword.setPassword(TEST_UPDATED_PASSWORD);
        String requestJson2 = convertObjectToStringForJson(userWithNewPassword);

        List<PasswordResetToken> token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());

        // try with random token
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_ENDPOINT + USER_CHANGE_PASSW_TOKEN_PATH, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson2))
                .andExpect(status().isBadRequest());

        //token not consumed
        token = resetTokenRepository.findAllByUserId(responseArray[0].getId());
        Assertions.assertEquals(1, token.size());

        ResultActions resultActionsAfterPasswordChange =
                this.mockMvc
                        .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

        responseString = resultActionsAfterPasswordChange.andReturn().getResponse().getContentAsString();
        responseArray = mapper.readValue(responseString, User[].class);

        //password did not change
        Assertions.assertFalse(passwordEncoder.matches(TEST_UPDATED_PASSWORD, responseArray[0].getPassword()));
    }
}
