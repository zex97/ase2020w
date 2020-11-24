package com.studyboard.repository;

import com.studyboard.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Find a single user entry by id.
     *
     * @param id the is of the user entry
     * @return user object with specified id
     */
    User findUserById(long id);

    /**
     * Find a single user entry by username.
     *
     * @param username the name of the user entry
     * @return user object with specified username
     */
    User findOneByUsername(String username);

    /**
     * Returns a list of all users.
     */
    List<User> findAll();

}
