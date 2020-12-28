package com.studyboard.validator;

import com.studyboard.FileStorageProperties;
import com.studyboard.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Component
public class FileValidator {

  private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);
  private String[] allowedTypes;

  @Autowired
  public FileValidator(FileStorageProperties fileStorageProperties) {
    this.allowedTypes = fileStorageProperties.getFileTypes();
  }

  public void validateFile(MultipartFile file) {
    try {
      validateFile(file.getOriginalFilename(), file.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void validateFile(String fileName, byte[] content) {

    if (fileName.isEmpty() || fileName.isBlank()) {
      throw new FileStorageException("File name can't be empty");
    }

    String type = fileName.substring(fileName.lastIndexOf('.'));

    if (!Arrays.asList(allowedTypes).contains(type)) {
      logger.warn("File type " + type + " not supported!");
      throw new FileStorageException(
          "Filetype " + type + " of file '" + fileName + "' is not supported by the application");
    }
    if (content.length == 0) {
      throw new FileStorageException("Uploaded file (" + fileName + ") is empty!");
    }

    if (StringUtils.uriDecode(fileName, StandardCharsets.UTF_8).contains("../")) {
      logger.warn("File can't contain '../' in the name (security threat)");
      throw new FileStorageException("File name contains illegal char sequence \"../\"");
    }
  }

  public void validatePath(Path path) throws FileStorageException {
    // check if folder already exists
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        throw new FileStorageException(
            "Failed to create separate folder for path: " + path.toAbsolutePath().toString());
      }
    }
  }
}
