package com.studyboard.space.security.service;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.model.User;

import java.util.List;

public interface UserService {

    /**
     * Find a single user entry by id.
     *
     * @param id of the user
     * @return the user object with the specified id
     */
    User getUser(Long id);

    /**
     * Returns a list of all users.
     */
    List<User> getAllUsers();

    /**
     * Create a single user entry
     *
     * @param user to create
     * @return created user entry
     */
    User createUser(User user) throws UniqueConstraintException;


    /**
     * Update user password
     *
     * @param user whose password should be updated
     * @return user with updated password
     */
    User updateUserPassword(User user);

    /**
     * Resets login attempts to 0
     *
     * @param id of the user
     * @return user with 0 login attempts
     */
    User resetLoginAttempts(Long id);

}
