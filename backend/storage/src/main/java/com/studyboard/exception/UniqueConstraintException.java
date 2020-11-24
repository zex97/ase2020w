package com.studyboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UniqueConstraintException extends Exception {

    public UniqueConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueConstraintException(String message) {
        super(message);
    }
}
