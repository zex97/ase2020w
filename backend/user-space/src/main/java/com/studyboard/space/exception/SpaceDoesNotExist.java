package com.studyboard.space.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Space not found.")
public class SpaceDoesNotExist extends RuntimeException {
}
