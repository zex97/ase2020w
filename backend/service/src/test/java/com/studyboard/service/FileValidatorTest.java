package com.studyboard.service;

import com.studyboard.FileStorageProperties;
import com.studyboard.exception.FileStorageException;
import com.studyboard.validator.FileValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = FileStorageProperties.class)
public class FileValidatorTest {

  private static final String INVALID_FILE_NAME_1 = "file../test.pdf";
  private static final String INVALID_FILE_TYPE = "test.zip";
  private static final String EMPTY_FILE_NAME = "";
  private static final String[] VALID_TYPES =
      new String[] {".jpeg", ".png", ".pdf", ".mp3", ".mp4"};
  private static final byte[] VALID_FILE_CONTENT = "Hello World!".getBytes(StandardCharsets.UTF_8);
  private static final byte[] INVALID_FILE_CONTENT = new byte[] {};
  private static final MockMultipartFile VALID_MULTIPART_FILE =
      new MockMultipartFile(
          "file", "file.pdf", MediaType.TEXT_PLAIN_VALUE, "Hello World!".getBytes());
  private static final MockMultipartFile INVALID_MULTIPART_FILE =
      new MockMultipartFile(
          "file", INVALID_FILE_NAME_1, MediaType.TEXT_PLAIN_VALUE, "Hello World!".getBytes());

  @Mock private FileStorageProperties fileStorageProperties;

  @InjectMocks private FileValidator fileValidator;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(fileStorageProperties.getFileTypes()).thenReturn(VALID_TYPES);
  }

  @Test
  public void validateCorrectFile() {
    fileValidator.validateFile(VALID_MULTIPART_FILE);
  }

  @Test
  public void validateFileWithInvalidName_throwsException() {
    MockMultipartFile multipartFile =
            new MockMultipartFile(
                    "file", INVALID_FILE_NAME_1, MediaType.TEXT_PLAIN_VALUE, "Hello World!".getBytes());
    FileStorageException exception = Assertions.assertThrows(
        FileStorageException.class,
        () -> fileValidator.validateFile(multipartFile));
    Assertions.assertTrue(exception.getMessage().contains("File name contains illegal char sequence \"../\""));
  }

  @Test
  public void validateFileWithEmptyContent_throwsException() {
    MockMultipartFile multipartFile =
        new MockMultipartFile("file", "file.pdf", MediaType.TEXT_PLAIN_VALUE, new byte[]{});
    FileStorageException exception = Assertions.assertThrows(
        FileStorageException.class,
        () -> fileValidator.validateFile(multipartFile));
    Assertions.assertTrue(exception.getMessage().contains("is empty!"));
  }

  @Test
  public void validateFileWithUnsupportedType_throwsException() {
    MockMultipartFile multipartFile =
            new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, VALID_FILE_CONTENT);
    FileStorageException exception = Assertions.assertThrows(
            FileStorageException.class,
            () -> fileValidator.validateFile(multipartFile));
    Assertions.assertTrue(exception.getMessage().contains(".txt"));
  }
}
