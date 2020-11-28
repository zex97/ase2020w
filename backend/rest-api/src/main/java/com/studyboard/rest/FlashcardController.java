package com.studyboard.rest;

import com.studyboard.dto.DeckDTO;
import com.studyboard.dto.FlashcardDTO;
import com.studyboard.flashcard.service.FlashcardService;
import com.studyboard.model.Flashcard;
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

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
  public List<DeckDTO> getAllDecks(@PathVariable(name = "userId") long userId) {
    return flashcardService.getAllDecks(userId).stream()
        .map(DeckDTO::of)
        .collect(Collectors.toList());
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}",
      method = RequestMethod.GET,
      produces = "application/json")
  public DeckDTO getOneDeck(
      @PathVariable(name = "userId") long userId, @PathVariable(name = "deckId") long deckId) {
    return DeckDTO.of(flashcardService.getOneDeck(userId, deckId));
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
  public ResponseEntity createDeck(
      @PathVariable(name = "userId") long userId, @RequestBody DeckDTO deckDTO) {
    System.out.println(deckDTO.toString());
    flashcardService.createDeck(userId, deckDTO.toDeck());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  public ResponseEntity editDeckName(
      @PathVariable(name = "userId") long userId, @RequestBody DeckDTO deckDTO) {
    flashcardService.updateDeckName(userId, deckDTO.toDeck());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/deck{deckId}/flashcards",
      method = RequestMethod.GET,
      produces = "application/json")
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
  public ResponseEntity deleteDeck(
      @PathVariable(name = "userId") long userId, @PathVariable(name = "deckId") long deckId) {
    flashcardService.deleteDeck(userId, deckId);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/{userId}/{deckId}/{flashcardId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  public ResponseEntity deleteFlashcard(
      @PathVariable(name = "userId") long userId,
      @PathVariable(name = "deckId") long deckId,
      @PathVariable(name = "flashcardId") long flashcardId) {
    flashcardService.deleteFlashcard(deckId, flashcardId);
    return ResponseEntity.ok().build();
  }
}
