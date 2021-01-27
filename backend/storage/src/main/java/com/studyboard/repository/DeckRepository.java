package com.studyboard.repository;

import com.studyboard.model.Deck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Long> {

    /**
     * Find all decks that one user made ordered by last time used.
     *
     * @param username is of the user who made decks
     * @return deck objects with ordered by last time used descending
     */
    List<Deck> findByUserUsernameOrderByLastTimeUsedDesc(String username);

    /**
     * Find a single deck by id.
     *
     * @param deckId is of the deck entry
     * @return deck object with specified id
     */
    Deck findDeckById(Long deckId);

    /**
     * Find all decks containing a part of the search parameter in their name
     *
     * @param username of the user who is searching for own deck
     * @param searchParam - search parameter for finding a deck
     * @return all decks which contain the parameter in their name
     */
    List<Deck> findByUserUsernameAndNameContainingOrderByLastTimeUsedDesc(String username, String searchParam);
}
