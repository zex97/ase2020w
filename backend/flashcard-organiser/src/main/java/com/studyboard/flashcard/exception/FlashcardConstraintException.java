package com.studyboard.flashcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Confidence level value inappropriate.")
public class FlashcardConstraintException extends Exception {

    public FlashcardConstraintException(String message) {
        super(message);
    }
}
