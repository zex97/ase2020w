package com.studyboard.rest;

import com.studyboard.flashcard.service.SimpleFlashcardService;
import com.studyboard.model.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/decks")
public class DeckController {

    @Autowired
    private SimpleFlashcardService service;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public List<Deck> getAllDecks(){
        return service.getAllDecks();
    }

    @RequestMapping(value = "/{deckId}", method = RequestMethod.GET, produces = "application/json")
    public Deck getOneDeck(@PathVariable(name = "deckId") long deckId){
        return service.getOneDeck(deckId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createDeck(@RequestBody Deck deck) {
        service.createDeck(deck);
        return ResponseEntity.ok().build();
    }

 }
