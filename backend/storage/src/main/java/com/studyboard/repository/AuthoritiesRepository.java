package com.studyboard.repository;

import com.studyboard.model.Authorities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthoritiesRepository extends CrudRepository<Authorities, Long> {

    /**
     * Find a single authority entry by id.
     *
     * @param id the is of the Authorities entry
     * @return Optional containing the Authorities entry
     */
    Optional<Authorities> findOneById(Long id);

    /**
     * Find a single authority entry by username.
     *
     * @param username the username of the Authorities entry
     * @return Optional containing the Authorities entry
     */
    Optional<Authorities> findOneByUsername(String username);


}
