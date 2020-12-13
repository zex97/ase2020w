package com.studyboard.rest;

import com.studyboard.dto.DeckDTO;
import com.studyboard.dto.FlashcardDTO;
import com.studyboard.flashcard.exception.FlashcardConstraintException;
import com.studyboard.flashcard.service.FlashcardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(
            value = "Get all decks associated with specific user id.",
            authorizations = {@Authorization(value = "apiKey")})
    public List<DeckDTO> getAllDecks(@PathVariable(name = "username") String username) {
        return flashcardService.getAllDecks(username).stream()
                .map(DeckDTO::of)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/deck{deckId}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(
            value = "Get a deck with a specific id.",
            authorizations = {@Authorization(value = "apiKey")})
    public DeckDTO getOneDeck(@PathVariable(name = "deckId") Long deckId) {
        return DeckDTO.of(flashcardService.findDeckById(deckId));
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(
            value = "Create deck associated to a user specified in the DTO",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity createDeck(
            @RequestBody DeckDTO deckDTO) {
        flashcardService.createDeck(deckDTO.toDeck());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{deckId}",
            method = RequestMethod.PUT,
            produces = "application/json")
    @ApiOperation(
            value = "Edit deck with a specific id.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity editDeckName(
            @RequestBody DeckDTO deckDTO) {
        flashcardService.updateDeckName(deckDTO.toDeck());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{deckId}/flashcards",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get all flashcards associated with specific user and deck id.",
            authorizations = {@Authorization(value = "apiKey")})
    public List<FlashcardDTO> getAllFlashcards(
            @PathVariable(name = "deckId") long deckId) {
        return flashcardService.getAllFlashcardsOfDeck(deckId).stream()
                .map(FlashcardDTO::FlashcardDTOFromFlashcard)
                .collect(Collectors.toList());
    }

    @RequestMapping(
            value = "/{deckId}/flashcards/{size}",
            method = RequestMethod.GET,
            produces = "application/json")
    public List<FlashcardDTO> getFlashcardsForRevision(
            @PathVariable(name = "deckId") long deckId,
            @PathVariable(name = "size") int size) {
        return flashcardService.getFlashcardsForRevision(deckId, size).stream()
                .map(FlashcardDTO::FlashcardDTOFromFlashcard)
                .collect(Collectors.toList());
    }

    @RequestMapping(
            value = "/{deckId}/flashcard{flashcardId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Get flashcard with specific flashcard, user and deck id.",
            authorizations = {@Authorization(value = "apiKey")})
    public FlashcardDTO getOneFlashcard(
            @PathVariable(name = "deckId") long deckId,
            @PathVariable(name = "flashcardId") long flashcardId) {
        return FlashcardDTO.FlashcardDTOFromFlashcard(
                flashcardService.getOneFlashcard(deckId, flashcardId));
    }

    @RequestMapping(
            value = "/{deckId}",
            method = RequestMethod.POST,
            produces = "application/json")
    @ApiOperation(
            value = "Create a flashcard.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity createFlashcard(
            @PathVariable(name = "deckId") long deckId,
            @RequestBody FlashcardDTO flashcardDTO) {
        System.out.println(flashcardDTO.toString());
        flashcardService.createFlashcard(deckId, flashcardDTO.FlashcardFromFlashcardDTO());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{deckId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(
            value = "Delete deck.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteDeck(
            @PathVariable(name = "deckId") long deckId) {
        flashcardService.deleteDeck(deckId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{deckId}/{flashcardId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(
            value = "Delete flashcard.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteFlashcard(
            @PathVariable(name = "deckId") long deckId,
            @PathVariable(name = "flashcardId") long flashcardId) {
        flashcardService.deleteFlashcard(deckId, flashcardId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{deckId}/flashcard{flashcardId}",
            method = RequestMethod.PUT,
            produces = "application/json")
    @ApiOperation(
            value =
                    "Edit or rate the flashcard based on personal confidence level with the value between 1 and 5.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity editFlashcard(
            @PathVariable(name = "deckId") long deckId,
            @RequestBody FlashcardDTO flashcardDTO)
            throws FlashcardConstraintException {
        flashcardService.editFlashcard(deckId, flashcardDTO.FlashcardFromFlashcardDTO());
        return ResponseEntity.ok().build();
    }
}
