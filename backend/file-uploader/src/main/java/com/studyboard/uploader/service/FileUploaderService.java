package com.studyboard.uploader.service;

import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.StorageFileNotFoundException;
import com.studyboard.uploader.exception.FileStorageException;
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

/**
 * Service used to manage user uploaded files. Important: All files saved in the rootLocation, user
 * directories remains to be implemented
 */
@Service
public class FileUploaderService implements FileUploader {

  private final Path rootLocation;

  @Autowired
  public FileUploaderService(FileStorageProperties fileStorageProperties) {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
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

  /**
   * Stores the all files in the uploads directory Should be updated once the users are available
   */
  @Override
  public String store(MultipartFile file) {
    String fileName = file.getOriginalFilename();

    if (file.isEmpty()) {
      throw new FileStorageException("Uploaded file (" + fileName + ") is empty!");
    }

    if (StringUtils.uriDecode(file.getOriginalFilename(), StandardCharsets.UTF_8).contains("../")) {
      throw new FileStorageException("File name contains illegal char sequence \"../\"");
    }

    Path uploadFilePath =
        this.rootLocation
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
  public Stream<Resource> loadAll() {
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
        throw new StorageFileNotFoundException(
            "File (" + fileName + " could not be read, or doesn't exist");
      }

    } catch (MalformedURLException e) {
      throw new FileStorageException("Could not read the file (" + fileName + ")", e);
    }
  }

  @Override
  public void deleteUserFile(String fileName) {
    Path filePath = load(fileName);

    try {
      Files.delete(filePath);
    } catch (NoSuchFileException e) {
      System.out.println("vla");
      throw new StorageFileNotFoundException("File(" + fileName + ") not in the directory");
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete file(" + fileName + ")");
    }
  }

  /**
   * In case we want to delete a user, we delete his directory Username required for file deletion
   */
  @Override
  public void deleteUserFolder() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }
}
