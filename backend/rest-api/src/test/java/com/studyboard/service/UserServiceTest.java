package com.studyboard.service;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.Authorities;
import com.studyboard.model.User;
import com.studyboard.repository.AuthoritiesRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.user.service.SimpleUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final User TEST_USER_1 = new User("testUsername1", "testPassword1", "user1@email.com", 2, "USER", true);
    private static final User TEST_USER_2 = new User("testUsername2", "testPassword2", "user2@email.com", 0, "USER", true);
    private static final Long TEST_ID_1 = 1L;
    private static final String TEST_UPDATED_PASSWORD = "updatedPassword";
    private static final String TEST_HASHED_PASSWORD = "hashedPassword";


    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthoritiesRepository authoritiesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SimpleUserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void findAllUsersReturnsUsersCorrectly() {

        User user = new User(TEST_USER_1);

        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> response = userService.getAllUsers();
        User storedUser = response.get(0);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(TEST_USER_1.getUsername(), storedUser.getUsername());
        Assertions.assertEquals(TEST_USER_1.getPassword(), storedUser.getPassword());
        Assertions.assertEquals(TEST_USER_1.getEmail(), storedUser.getEmail());
        Assertions.assertEquals(TEST_USER_1.getLoginAttempts(), storedUser.getLoginAttempts());
        Assertions.assertEquals(TEST_USER_1.getEnabled(), storedUser.getEnabled());
        Assertions.assertEquals(TEST_USER_1.getRole(), storedUser.getRole());
    }

    @Test
    public void findUserByIdSuccessfully() {

        User user = new User(TEST_USER_1);
        user.setId(TEST_ID_1);

        Mockito.when(userRepository.findUserById(TEST_ID_1)).thenReturn(user);

        User storedUser = userService.getUser(TEST_ID_1);

        Assertions.assertNotNull(storedUser);
        Assertions.assertEquals(TEST_USER_1.getUsername(), storedUser.getUsername());
        Assertions.assertEquals(TEST_USER_1.getPassword(), storedUser.getPassword());
        Assertions.assertEquals(TEST_USER_1.getEmail(), storedUser.getEmail());
        Assertions.assertEquals(TEST_USER_1.getLoginAttempts(), storedUser.getLoginAttempts());
        Assertions.assertEquals(TEST_USER_1.getEnabled(), storedUser.getEnabled());
        Assertions.assertEquals(TEST_USER_1.getRole(), storedUser.getRole());
    }

    @Test
    public void searchUserThatDoesNotExist_ThrowsUserDoesNotExist() {
        Assertions.assertThrows(UserDoesNotExist.class, () -> {
            userService.getUser(TEST_ID_1);
        });
    }

    @Test
    public void updateUserThatDoesNotExist_ThrowsUserDoesNotExist() {
        User user = new User();
        user.setId(TEST_ID_1);
        Assertions.assertThrows(UserDoesNotExist.class, () -> {
            userService.updateUserPassword(user);
        });
    }

    @Test
    public void resetLoginAttemptsOfUserThatDoesNotExist_ThrowsUserDoesNotExist() {
        Assertions.assertThrows(UserDoesNotExist.class, () -> {
            userService.resetLoginAttempts(TEST_ID_1);
        });
    }

    @Test
    public void createUserWithExistingUsername_ThrowsUniqueConstraintException() throws UniqueConstraintException {
        User user1 = new User(TEST_USER_1);

        Mockito.when(passwordEncoder.encode(user1.getPassword())).thenReturn(user1.getPassword());
        Mockito.when(userRepository.save(user1)).thenReturn(user1);
        Mockito.when(authoritiesRepository.save(new Authorities(user1.getUsername(), "USER"))).thenReturn(new Authorities(user1.getUsername(), "USER"));
        userService.createUser(user1);


        User user2 = new User(TEST_USER_2);
        user2.setUsername(TEST_USER_1.getUsername());

        Mockito.when(authoritiesRepository.save(new Authorities(user2.getUsername(), "USER"))).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(UniqueConstraintException.class, () -> {
            userService.createUser(user2);
        });
    }

    @Test
    public void updatePasswordSuccessfully() {
        User user = new User(TEST_USER_1);
        user.setId(TEST_ID_1);

        User userWithNewPassword = new User(TEST_USER_1);
        userWithNewPassword.setId(TEST_ID_1);
        userWithNewPassword.setPassword(TEST_UPDATED_PASSWORD);

        Mockito.when(passwordEncoder.encode(userWithNewPassword.getPassword())).thenReturn(TEST_HASHED_PASSWORD);
        Mockito.when(userRepository.findUserById(TEST_ID_1)).thenReturn(user);
        user.setPassword(TEST_HASHED_PASSWORD);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.updateUserPassword(userWithNewPassword);

        Assertions.assertEquals(user, userService.updateUserPassword(userWithNewPassword));
    }
}
