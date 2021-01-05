package com.studyboard.repository;


import com.studyboard.model.Space;
import com.studyboard.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("unit-test")
@DataJpaTest
public class UserSpaceRepositoryTest {

    private static final long USER_ID = 1L;
    private static final String USER_USERNAME = "userTest";
    private static final String USER_PASSWORD = "password1";
    private static final String USER_EMAIL = "demo1@email.com";
    private static final Integer USER_LOGIN_ATTEMPTS = 0;
    private static final Boolean USER_ENABLED = true;
    private static final String USER_ROLE = "ADMIN";

    private static final Long SPACE_ID = 2L;
    private static final String SPACE_NAME = "Test";
    private static final String SPACE_SEARCH_PARAMETER = "Test";

    private static final Long SPACE_ID_1 = 3L;
    private static final String SPACE_NAME_1 = "Test2";

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void repositorySavesSpaceCorrectly() {
        assertEquals(
                0, spaceRepository.findByUserUsername(USER_USERNAME).size());

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
        user.setRole(USER_ROLE);
        user.setEnabled(USER_ENABLED);
        userRepository.save(user);

        Space space = new Space();
        space.setId(SPACE_ID);
        space.setName(SPACE_NAME);
        space.setUser(user);
        spaceRepository.save(space);

        List<Space> spaces = spaceRepository.findByUserUsername(USER_USERNAME);

        Space storedSpace = spaces.get(0);
        assertEquals(SPACE_NAME, storedSpace.getName());
    }

    @Test
    public void repositoryFindsAllSpacesWithNameContainingSpecificStringCorrectly() {
        assertEquals(
                0, spaceRepository.findByUserUsername(USER_USERNAME).size());

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
        user.setRole(USER_ROLE);
        user.setEnabled(USER_ENABLED);
        userRepository.save(user);

        Space space = new Space();
        space.setId(SPACE_ID);
        space.setName(SPACE_NAME);
        space.setUser(user);
        spaceRepository.save(space);

        List<Space> spaces = spaceRepository.findByUserUsernameAndNameContaining(USER_USERNAME, SPACE_SEARCH_PARAMETER);

        Space storedSpace = spaces.get(0);
        assertEquals(spaces.size(), 1);
        assertEquals(SPACE_NAME, storedSpace.getName());
    }


    @Test
    public void repositoryFindsAllUserSpacesCorrectly() {
        assertEquals(
                0, spaceRepository.findByUserUsername(USER_USERNAME).size());

        User user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.setLoginAttempts(USER_LOGIN_ATTEMPTS);
        user.setRole(USER_ROLE);
        user.setEnabled(USER_ENABLED);
        userRepository.save(user);

        Space space = new Space();
        space.setId(SPACE_ID);
        space.setName(SPACE_NAME);
        space.setUser(user);
        spaceRepository.save(space);

        Space space1 = new Space();
        space1.setId(SPACE_ID_1);
        space1.setName(SPACE_NAME_1);
        space1.setUser(user);
        spaceRepository.save(space1);

        List<Space> spaces = spaceRepository.findByUserUsernameAndNameContaining(USER_USERNAME, SPACE_SEARCH_PARAMETER);

        assertEquals(spaces.size(), 2);
    }
}
