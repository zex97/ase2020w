package com.studyboard.storage;

import com.studyboard.model.User;
import com.studyboard.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@EnableJpaRepositories(basePackages = {"com.studyboard.repository"})
public class UserRepositoryTest {

    private static final long TEST_USER_ID_1 = 1L;
    private static final String TEST_USER_USERNAME_1 = "username1";
    private static final String TEST_USER_PASSWORD_1 = "password1";
    private static final String TEST_USER_EMAIL_1 = "demo1@email.com";
    private static final Integer TEST_USER_LOGIN_ATTEMPTS_1 = 0;
    private static final String TEST_ROLE_1 = "USER";
    private static final boolean TEST_ENABLED_1 = true;

    private static final long TEST_USER_ID_2 = 2L;
    private static final String TEST_USER_USERNAME_2 = "username2";
    private static final String TEST_USER_PASSWORD_2 = "password2";
    private static final String TEST_USER_EMAIL_2 = "demo2@email.com";
    private static final Integer TEST_USER_LOGIN_ATTEMPTS_2 = 2;
    private static final String TEST_ROLE_2 = "USER";
    private static final boolean TEST_ENABLED_2 = true;


    @Autowired
    private UserRepository userRepository;

    @Test
    public void repositorySavesUserCorrectly() {
        Assertions.assertEquals(0, userRepository.findAll().size());

        User user = new User();
        user.setId(TEST_USER_ID_1);
        user.setUsername(TEST_USER_USERNAME_1);
        user.setPassword(TEST_USER_PASSWORD_1);
        user.setEmail(TEST_USER_EMAIL_1);
        user.setLoginAttempts(TEST_USER_LOGIN_ATTEMPTS_1);
        user.setEnabled(TEST_ENABLED_1);
        user.setRole(TEST_ROLE_1);
        userRepository.save(user);

        Assertions.assertEquals(1, userRepository.findAll().size());

        User storedUser = userRepository.findUserById(TEST_USER_ID_1);

        Assertions.assertEquals(TEST_USER_ID_1, storedUser.getId());
        Assertions.assertEquals(TEST_USER_USERNAME_1, storedUser.getUsername());
        Assertions.assertEquals(TEST_USER_PASSWORD_1, storedUser.getPassword());
        Assertions.assertEquals(TEST_USER_EMAIL_1, storedUser.getEmail());
        Assertions.assertEquals(TEST_USER_LOGIN_ATTEMPTS_1, storedUser.getLoginAttempts());
        Assertions.assertEquals(TEST_ENABLED_1, storedUser.getEnabled());
        Assertions.assertEquals(TEST_ROLE_1, storedUser.getRole());
    }

    @Test
    public void findOneByUsernameReturnsCorrectUser() {
        Assertions.assertEquals(0, userRepository.findAll().size());

        User user = new User();
        user.setId(TEST_USER_ID_1);
        user.setUsername(TEST_USER_USERNAME_1);
        user.setPassword(TEST_USER_PASSWORD_1);
        user.setEmail(TEST_USER_EMAIL_1);
        user.setLoginAttempts(TEST_USER_LOGIN_ATTEMPTS_1);
        user.setEnabled(TEST_ENABLED_1);
        user.setRole(TEST_ROLE_1);
        userRepository.save(user);

        Assertions.assertEquals(1, userRepository.findAll().size());

        User storedUser = userRepository.findOneByUsername(TEST_USER_USERNAME_2);

        Assertions.assertNull(storedUser);

        storedUser = userRepository.findOneByUsername(TEST_USER_USERNAME_1);

        Assertions.assertNotNull(storedUser);
        Assertions.assertEquals(TEST_USER_ID_1, storedUser.getId());
        Assertions.assertEquals(TEST_USER_USERNAME_1, storedUser.getUsername());
        Assertions.assertEquals(TEST_USER_PASSWORD_1, storedUser.getPassword());
        Assertions.assertEquals(TEST_USER_EMAIL_1, storedUser.getEmail());
        Assertions.assertEquals(TEST_USER_LOGIN_ATTEMPTS_1, storedUser.getLoginAttempts());
        Assertions.assertEquals(TEST_ENABLED_1, storedUser.getEnabled());
        Assertions.assertEquals(TEST_ROLE_1, storedUser.getRole());

        User user2 = new User();
        user2.setId(TEST_USER_ID_2);
        user2.setUsername(TEST_USER_USERNAME_2);
        user2.setPassword(TEST_USER_PASSWORD_2);
        user2.setEmail(TEST_USER_EMAIL_2);
        user2.setLoginAttempts(TEST_USER_LOGIN_ATTEMPTS_2);
        user2.setEnabled(TEST_ENABLED_2);
        user2.setRole(TEST_ROLE_2);
        userRepository.save(user2);

        Assertions.assertEquals(2, userRepository.findAll().size());

        User storedUser2 = userRepository.findOneByUsername(TEST_USER_USERNAME_2);

        Assertions.assertNotNull(storedUser2);
        Assertions.assertEquals(TEST_USER_ID_2, storedUser2.getId());
        Assertions.assertEquals(TEST_USER_USERNAME_2, storedUser2.getUsername());
        Assertions.assertEquals(TEST_USER_PASSWORD_2, storedUser2.getPassword());
        Assertions.assertEquals(TEST_USER_EMAIL_2, storedUser2.getEmail());
        Assertions.assertEquals(TEST_USER_LOGIN_ATTEMPTS_2, storedUser2.getLoginAttempts());
        Assertions.assertEquals(TEST_ENABLED_2, storedUser.getEnabled());
        Assertions.assertEquals(TEST_ROLE_2, storedUser.getRole());

    }

}
