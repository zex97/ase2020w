package com.studyboard.uploader.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.FileStorageException;
import com.studyboard.uploader.exception.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/** Service used to manage user files. Performs file saving, loading, and deletion */
@Service
public class FileUploaderService implements FileUploader {

  private final Logger logger = LoggerFactory.getLogger(FileUploaderService.class);
  private final Path rootLocation;
  private final UserRepository userRepository;
  private final SpaceRepository spaceRepository;

  @Autowired
  public FileUploaderService(
      FileStorageProperties fileStorageProperties,
      UserRepository userRepository,
      SpaceRepository spaceRepository) {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    this.userRepository = userRepository;
    this.spaceRepository = spaceRepository;
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
  public String store(MultipartFile file, long spaceId) {
    Space space = spaceRepository.findSpaceById(spaceId);
    String fileName = file.getOriginalFilename();

    // create folder path for each individual user
    User user = space.getUser();
    Path completeUserPath = rootLocation.resolve(Paths.get(user.getUsername()));

    // check if folder already exists
    if (!Files.exists(completeUserPath)) {
      try {
        Files.createDirectories(completeUserPath);
      } catch (IOException e) {
        throw new FileStorageException(
            "Failed to create separate folder for user: " + user.getUsername());
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
            .resolve(user.getUsername())
            .resolve(Paths.get(file.getOriginalFilename()))
            .normalize()
            .toAbsolutePath();

    try {
      Files.copy(file.getInputStream(), uploadFilePath, StandardCopyOption.REPLACE_EXISTING);

    } catch (IOException e) {
      throw new FileStorageException("Failed to store file (" + fileName + ")!", e);
    }

    storeRefToNewDocument(space, uploadFilePath);

    return fileName;
  }

  private void storeRefToNewDocument(Space space, Path path) {

    Document document = null;
    for (Document d : space.getDocuments()) {
      if (d.getFilePath().equals(path.toAbsolutePath().toString())) {
        document = d;
      }
    }

    if (document == null) {
      document = new Document();
      document.setFilePath(path.toAbsolutePath().toString());
      // use name without the file extension
      document.setName(path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".")));
      document.setSpace(space);
      // TODO: check if transcription is necessary
      document.setNeedsTranscription(false);
      document.setTranscription(null);

      List<Document> docList = space.getDocuments();
      docList.add(document);
      space.setDocuments(docList);
      spaceRepository.save(space);

      logger.info(
          "Created new document for file("
              + path.getFileName().toString()
              + ") in space("
              + space.getName()
              + ") of user "
              + space.getUser().getUsername());
    } else {

      logger.info(
          "File ("
              + path.getFileName().toString()
              + ") already exists, overriding the file content.");
    }
  }

  @Override
  public Path load(String filename, String userName) {
    return rootLocation.resolve(userName).resolve(filename);
  }

  @Override
  public Resource loadAsResource(Space space, String fileName) {
    String userName = space.getUser().getUsername();
    Path filePath = load(fileName, userName);
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
  public void deleteUserFile(String fileName, Space space) {
    Path filePath = load(fileName, space.getUser().getUsername());

    try {
      Files.delete(filePath);
    } catch (NoSuchFileException e) {
      throw new StorageFileNotFoundException("File(" + fileName + ") not in the directory");
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete file(" + fileName + ")");
    }

    /*Document document =
        documentRepository.findByFilePath(filePath.toAbsolutePath().toString()).orElse(null);
    if (document != null) {
      documentRepository.delete(document);
    } else {
      throw new StorageFileNotFoundException("File(" + fileName + ") could not be found");
    }*/
  }

  @Override
  public void deleteUserFolder(String userName) {
    FileSystemUtils.deleteRecursively(rootLocation.resolve(userName).toFile());
  }
}
