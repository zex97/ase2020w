package com.studyboard.repository;

import com.studyboard.model.Deck;
import com.studyboard.model.Flashcard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {

    @Query("SELECT f FROM Flashcard f WHERE :deck in elements(f.decks)")
    List<Flashcard> findByDeck(Deck deck);

    Flashcard findFlashcardById(long id);
}
