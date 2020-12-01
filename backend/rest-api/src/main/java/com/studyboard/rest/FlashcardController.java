package com.studyboard.rest;

import com.studyboard.dto.DeckDTO;
import com.studyboard.dto.FlashcardDTO;
import com.studyboard.flashcard.exception.FlashcardConstraintException;
import com.studyboard.flashcard.service.FlashcardService;
import com.studyboard.model.Flashcard;
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

  @Autowired private FlashcardService flashcardService;

  @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
  @ApiOperation(
      value = "Get all decs associated with specific user id.",
      authorizations = {@Authorization(value = "apiKey")})
  public List<DeckDTO> getAllDecks(@PathVariable(name = "username") String username) {
    return flashcardService.getAllDecks(username).stream()
        .map(DeckDTO::of)
        .collect(Collectors.toList());
  }

  @RequestMapping(
      value = "/{username}/deck{deckId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ApiOperation(
      value = "Get deck associated with specific user username.",
      authorizations = {@Authorization(value = "apiKey")})
  public DeckDTO getOneDeck(
      @PathVariable(name = "username") String username,
      @PathVariable(name = "deckId") long deckId) {
    return DeckDTO.of(flashcardService.getOneDeck(username, deckId));
  }

  @RequestMapping(value = "/{username}", method = RequestMethod.POST, produces = "application/json")
  @ApiOperation(
      value = "Create deck associated to a user specified in the DTO",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity createDeck(
      @PathVariable(name = "username") String username, @RequestBody DeckDTO deckDTO) {
    flashcardService.createDeck(username, deckDTO.toDeck());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{username}/deck{deckId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ApiOperation(
      value = "Edit deck associated with specific user username.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity editDeckName(
      @PathVariable(name = "username") String username, @RequestBody DeckDTO deckDTO) {
    flashcardService.updateDeckName(username, deckDTO.toDeck());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}/flashcards",
      method = RequestMethod.GET,
      produces = "application/json")
  @ApiOperation(
      value = "Get all flashcards associated with specific user and deck id.",
      authorizations = {@Authorization(value = "apiKey")})
  public List<FlashcardDTO> getAllFlashcards(
      @PathVariable(name = "userId") long userId, @PathVariable(name = "deckId") long deckId) {
    return flashcardService.getAllFlashcardsOfDeck(deckId).stream()
        .map(FlashcardDTO::FlashcardDTOFromFlashcard)
        .collect(Collectors.toList());
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}/flashcard{flashcardId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ApiOperation(
      value = "Get flashcard with specific flashcard, user and deck id.",
      authorizations = {@Authorization(value = "apiKey")})
  public FlashcardDTO getOneFlashcard(
      @PathVariable(name = "userId") long userId,
      @PathVariable(name = "deckId") long deckId,
      @PathVariable(name = "flashcardId") long flashcardId) {
    return FlashcardDTO.FlashcardDTOFromFlashcard(
        flashcardService.getOneFlashcard(deckId, flashcardId));
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ApiOperation(
      value = "Create a flashcard.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity createFlashcard(
      @PathVariable(name = "userId") long userId,
      @PathVariable(name = "deckId") long deckId,
      @RequestBody FlashcardDTO flashcardDTO) {
    System.out.println(flashcardDTO.toString());
    flashcardService.createFlashcard(deckId, flashcardDTO.FlashcardFromFlashcardDTO());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/{deckId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ApiOperation(
      value = "Delete deck.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity deleteDeck(
      @PathVariable(name = "userId") long userId, @PathVariable(name = "deckId") long deckId) {
    flashcardService.deleteDeck(userId, deckId);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/{deckId}/{flashcardId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ApiOperation(
      value = "Delete flashcard.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity deleteFlashcard(
      @PathVariable(name = "userId") long userId,
      @PathVariable(name = "deckId") long deckId,
      @PathVariable(name = "flashcardId") long flashcardId) {
    flashcardService.deleteFlashcard(deckId, flashcardId);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}/flashcard{flashcardId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ApiOperation(
      value =
          "Rate the flashcard based on personal confidence level with the value between 1 and 5.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity rateFlashcard(
      @PathVariable(name = "userId") long userId,
      @PathVariable(name = "deckId") long deckId,
      @RequestBody FlashcardDTO flashcardDTO)
      throws FlashcardConstraintException {
    flashcardService.rateFlashcard(deckId, flashcardDTO.FlashcardFromFlashcardDTO());
    return ResponseEntity.ok().build();
  }
}
