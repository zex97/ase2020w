package com.studyboard.space.transcriber.exception;

/**
 * This exception class describes all exceptions thrown by transcriber module.
 *
 */
public class TranscriberException extends RuntimeException {
    public TranscriberException(String message) {
        super(message);
    }
    public TranscriberException(String message, Throwable cause) {
        super(message, cause);
    }
}
