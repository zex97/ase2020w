package com.studyboard.repository;

import com.studyboard.model.Space;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends CrudRepository<Space, Long> {

    List<Space> findAll();

    List<Space> findByUserUsername(String username);

    Space findSpaceById(long spaceId);
}
