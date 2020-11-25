package com.studyboard.uploader.service;

import com.studyboard.repository.UserRepository;
import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.FileStorageException;
import com.studyboard.uploader.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.*;
import java.util.stream.Stream;

/** Service used to manage user files. Performs file saving, loading, and deletion */
@Service
public class FileUploaderService implements FileUploader {

  private final Path rootLocation;

  private final UserRepository userRepository;

  @Autowired
  public FileUploaderService(
      FileStorageProperties fileStorageProperties, UserRepository userRepository) {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    this.userRepository = userRepository;
  }

  @Override
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new FileStorageException("Unable to initialize local storage");
    }
  }

  @Override
  public String store(MultipartFile file, long userId) {

    String fileName = file.getOriginalFilename();

    // create folder path for each individual user
    Path userName = Paths.get(userRepository.findUserById(userId).getUsername());
    Path completeUserPath = rootLocation.resolve(userName);

    // check if folder already exists
    if (!Files.exists(completeUserPath)) {
      try {
        Files.createDirectories(completeUserPath);
      } catch (IOException e) {
        throw new FileStorageException("Failed to create separate folder for user: " + userName);
      }
    }

    if (file.isEmpty()) {
      throw new FileStorageException("Uploaded file (" + fileName + ") is empty!");
    }

    if (StringUtils.uriDecode(file.getOriginalFilename(), StandardCharsets.UTF_8).contains("../")) {
      throw new FileStorageException("File name contains illegal char sequence \"../\"");
    }

    Path uploadFilePath =
        this.rootLocation
            .resolve(userName)
            .resolve(Paths.get(file.getOriginalFilename()))
            .normalize()
            .toAbsolutePath();

    try {
      Files.copy(file.getInputStream(), uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new FileStorageException("Failed to store file (" + fileName + ")!", e);
    }

    return fileName;
  }

  @Override
  public Stream<Resource> loadAll(long userId) {
    return null;
  }

  @Override
  public Path load(String filename, long userId) {
    return rootLocation
        .resolve(userRepository.findUserById(userId).getUsername())
        .resolve(filename);
  }

  @Override
  public Resource loadAsResource(String fileName, long userId) {
    Path filePath = load(fileName, userId);
    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageFileNotFoundException(
            "File (" + fileName + " could not be read, or doesn't exist");
      }

    } catch (MalformedURLException e) {
      throw new FileStorageException("Could not read the file (" + fileName + ")", e);
    }
  }

  @Override
  public void deleteUserFile(String fileName, long userId) {
    Path filePath = load(fileName, userId);

    try {
      Files.delete(filePath);
    } catch (NoSuchFileException e) {
      throw new StorageFileNotFoundException("File(" + fileName + ") not in the directory");
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete file(" + fileName + ")");
    }
  }

  @Override
  public void deleteUserFolder(long userId) {
    FileSystemUtils.deleteRecursively(
        rootLocation.resolve(userRepository.findUserById(userId).getUsername()).toFile());
  }
}
