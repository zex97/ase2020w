package com.studyboard.flashcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Deck not found.")
public class DeckDoesNotExist extends RuntimeException {
}
