package com.studyboard.rest;

import com.studyboard.flashcard.service.FlashcardService;
import com.studyboard.model.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/decks")
public class FlashcardController {

    @Autowired
    private FlashcardService service;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public List<Deck> getAllDecks(@PathVariable (name="userId") long userId){
        return service.getAllDecks(userId);
    }

    @RequestMapping(value = "/{userId}/{deckId}", method = RequestMethod.GET, produces = "application/json")
    public Deck getOneDeck(@PathVariable (name="userId") long userId, @PathVariable(name = "deckId") long deckId){
        return service.getOneDeck(userId, deckId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createDeck(@PathVariable (name="userId") long userId, @RequestBody Deck deck) {
        service.createDeck(userId, deck);
        return ResponseEntity.ok().build();
    }

 }
