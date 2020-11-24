package com.studyboard.uploader.service;

import com.studyboard.uploader.StorageProperties;
import com.studyboard.uploader.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Service used to manage user uploaded files.
 * Important: All files saved in the rootLocation, user directories remains to be implemented
 * */
@Service
public class FileUploaderService implements FileUploader {

  private final Path rootLocation;

  @Autowired
  public FileUploaderService(StorageProperties storageProperties) {
    this.rootLocation = Paths.get(storageProperties.getLocation());
  }

  @Override
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new StorageException("Unable to initialize local storage");
    }
  }

  /**
   * Stores the all files in the uploads directory Should be updated once the users are available
   */
  @Override
  public String store(MultipartFile file) {
    String fileName = file.getOriginalFilename();

    if (file.isEmpty()) {
      throw new StorageException("Uploaded file (" + fileName + ") is empty!");
    }

    if (StringUtils.uriDecode(file.getOriginalFilename(), StandardCharsets.UTF_8).contains("../")) {
      throw new StorageException("File name contains illegal char sequence \"../\"");
    }

    try {
      Files.copy(
          file.getInputStream(),
          this.rootLocation
              .resolve(Paths.get(file.getOriginalFilename()))
              .normalize()
              .toAbsolutePath(),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file (" + fileName + ")!", e);
    }

    return fileName;
  }

  @Override
  public Stream<Path> loadAll() {
    return null;
  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String fileName) {
    Path filePath = load(fileName);
    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException("File (" + fileName + " could not be read, or doesn't exist");
      }

    } catch (MalformedURLException e) {
      throw new StorageException("Could not read the file (" + fileName + ")", e);
    }
  }


  /**
   * Remains for the possibility of deleting the user
   * */
  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }
}
