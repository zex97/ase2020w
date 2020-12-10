package com.studyboard.repository;

import com.studyboard.model.Deck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Long> {

    List<Deck> findByUserUsernameOrderByLastTimeUsedDesc(String username);

    Deck findDeckById(Long deckId);
}
