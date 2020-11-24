package com.studyboard.uploader.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundExceptionFile extends FileStorageException {
  public FileNotFoundExceptionFile(String message) {
    super(message);
  }

  public FileNotFoundExceptionFile(String message, Throwable cause) {
    super(message, cause);
  }
}
