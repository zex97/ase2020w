package com.studyboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Tag should not be NULL, empty or already exist for the document.")
public class IllegalTagException extends RuntimeException {
    public IllegalTagException(String message) {
        super(message);
    }
}
