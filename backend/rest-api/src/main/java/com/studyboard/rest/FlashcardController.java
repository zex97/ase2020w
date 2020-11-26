package com.studyboard.rest;

import com.studyboard.dto.DeckDTO;
import com.studyboard.flashcard.service.FlashcardService;
import com.studyboard.model.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value="/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService service;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public void getAllDecks(){
        System.out.println("CONN");
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public List<DeckDTO> getAllDecks(@PathVariable (name="userId") long userId){
        return service.getAllDecks(userId).stream().map(DeckDTO::of).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{userId}/deck{deckId}", method = RequestMethod.GET, produces = "application/json")
    public DeckDTO getOneDeck(@PathVariable (name="userId") long userId, @PathVariable(name = "deckId") long deckId){
        return DeckDTO.of(service.getOneDeck(userId, deckId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createDeck(@PathVariable (name="userId") long userId, @RequestBody DeckDTO deckDTO) {
        System.out.println(deckDTO.getName());
        System.out.println(deckDTO.getUserDTO().getId());
        service.createDeck(userId, deckDTO.toDeck());
        return ResponseEntity.ok().build();
    }

 }
