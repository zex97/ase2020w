package com.studyboard.space.security.service;

public class UniqueConstraintException extends Exception {

    public UniqueConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueConstraintException(String message) {
        super(message);
    }
}
