package com.studyboard.uploader.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.transcriber.service.TranscriptionService;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/** Service used to manage user files. Performs file saving, loading, and deletion */
@Service
public class SimpleFileUploadService implements FileUploadService {

  private final Logger logger = LoggerFactory.getLogger(SimpleFileUploadService.class);
  private final Path rootLocation;
  private final SpaceRepository spaceRepository;
  private final TranscriptionService transcriptionService;

  @Autowired
  public SimpleFileUploadService(
          FileStorageProperties fileStorageProperties,
          UserRepository userRepository,
          SpaceRepository spaceRepository,
          TranscriptionService transcriptionService) {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    this.spaceRepository = spaceRepository;
    this.transcriptionService = transcriptionService;
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
    Path completeUserPath =
        rootLocation.resolve(Paths.get(user.getUsername())).normalize().toAbsolutePath();

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

    if (file.getOriginalFilename() != null) {
      if (StringUtils.uriDecode(file.getOriginalFilename(), StandardCharsets.UTF_8)
          .contains("../")) {
        throw new FileStorageException("File name contains illegal char sequence \"../\"");
      }
    } else {
      logger.error("File name is null!");
      throw new FileStorageException("File name is null!");
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

  /** Checks if the file type requires transcription. */
  private boolean isTranscriptionNeeded(String fileName) {
    List<String> transcriptionFormats = Arrays.asList(".mp3", ".mp4");
    String extension = fileName.substring(fileName.lastIndexOf('.'));
    return transcriptionFormats.contains(extension);
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
      document.setName(path.getFileName().toString());

      // extension is lost then
      document.setSpace(space);
      document.setNeedsTranscription(isTranscriptionNeeded(path.getFileName().toString()));
      document.setTranscription(null);

      // TODO: add transcriber trigger
        if (document.isNeedsTranscription()){
            transcriptionService.transcribe(document);
        }
      List<Document> docList = space.getDocuments();
      docList.add(document);
      space.setDocuments(docList);
      spaceRepository.save(space);

      logger.info(
          "Created new document for file '"
              + path.getFileName().toString()
              + "' in space("
              + space.getName()
              + ") of user ("
              + space.getUser().getUsername()
              + ")");
    } else {

      logger.info(
          "File '"
              + path.getFileName().toString()
              + "' already exists, overriding the file content.");
    }
  }

  @Override
  public Path load(String filename, String userName) {
    return rootLocation.resolve(userName).resolve(filename).normalize().toAbsolutePath();
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
        logger.debug("Reading of file '" + fileName + "' stored on path " + filePath + " failed");
        throw new StorageFileNotFoundException(
            "File '" + fileName + "' could not be read, or doesn't exist");
      }

    } catch (MalformedURLException e) {
      logger.error("File '" + fileName + "' has an invalid path (" + filePath + ")");
      throw new FileStorageException("Could not read the file (" + fileName + ")", e);
    }
  }

  @Override
  public void deleteUserFile(String fileName, long spaceId) {
    Space space = spaceRepository.findSpaceById(spaceId);
    Path filePath = load(fileName, space.getUser().getUsername());

    // if some other space also needs this document skip deletion of the file
    if (checkAllOtherSpaces(space, fileName)) {
      logger.debug(
          "File '" + fileName + "' not deleted because it is used by at least one other space.");
      return;
    }

    List<Document> list = space.getDocuments();

    // see if document is deleted, if not
    // skip deletion of file to avoid inconsistency
    if (list.stream().anyMatch(document -> document.getFilePath().equals(fileName))) {
      throw new FileStorageException(
          "Trying to delete a file '" + fileName + "' whose document still exists");
    }
    try {
      Files.delete(filePath);
      logger.info(
          "File '"
              + fileName
              + "' has been successfully deleted by user("
              + space.getUser().getUsername()
              + ") in space("
              + space.getName()
              + ").");

    } catch (NoSuchFileException e) {
      logger.debug("File '" + fileName + "' not in the directory");
      throw new StorageFileNotFoundException("File '" + fileName + "' not in the directory");
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete file '" + fileName + "'");
    }
  }

  @Override
  public void deleteUserFolder(String userName) {
    FileSystemUtils.deleteRecursively(rootLocation.resolve(userName).toFile());
  }

  /**
   * Iterates through all other user spaces to see if they use this document as well.
   *
   * @return true if any other space has a document based on this file
   */
  private boolean checkAllOtherSpaces(Space space, String fileName) {
    for (Space help : space.getUser().getSpaces()) {
      if (help.getId() != space.getId())
        for (Document document : help.getDocuments()) {
          if (document.getName().equals(fileName)) {
            return true;
          }
        }
    }
    return false;
  }
}
