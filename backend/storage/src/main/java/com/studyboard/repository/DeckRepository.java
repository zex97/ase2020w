package com.studyboard.repository;

import com.studyboard.model.Deck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Long> {

    //@Query(value = "SELECT * FROM deck WHERE ")
    List<Deck> findByUser(Long userId);

    List<Deck> findAll();

    Optional<Deck> findById(long deckId);

    @Override
    void deleteById(Long aLong);
}
