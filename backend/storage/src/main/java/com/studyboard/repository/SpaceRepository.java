package com.studyboard.repository;

import com.studyboard.model.Space;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends CrudRepository<Space, Long> {

    Optional<Space> findWithTagsById(long id);

    /**
     * @return a list of all spaces
     */
    List<Space> findAll();

    /**
     * Find a list of spaces by one user name
     *
     * @param username of user who created the spaces
     * @return list of user's spaces
     */
    List<Space> findByUserUsernameOrderByCreationDateDesc(String username);

    /**
     * Find a single space entry by id.
     *
     * @param spaceId is of the space entry
     * @return space object with specified id
     */
    Space findSpaceById(long spaceId);

    /**
     * Find all spaces containing a part of the search parameter in their name
     *
     * @param username of the user who is searching for own space
     * @param searchParam - search parameter for finding a space
     * @return all spaces which contain the parameter in their name
     */
    List<Space> findByUserUsernameAndNameContainingOrderByCreationDateDesc(String username, String searchParam);
}
