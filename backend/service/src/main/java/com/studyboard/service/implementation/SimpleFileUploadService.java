package com.studyboard.service.implementation;

import com.studyboard.FileStorageProperties;
import com.studyboard.exception.FileStorageException;
import com.studyboard.exception.StorageFileNotFoundException;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.DocumentRepository;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.service.FileUploadService;
import com.studyboard.service.TranscriptionService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Service used to manage user files. Performs file saving, loading, and deletion */
@Service
public class SimpleFileUploadService implements FileUploadService {

  private final Logger logger = LoggerFactory.getLogger(SimpleFileUploadService.class);
  private final Path rootLocation;
  private final SpaceRepository spaceRepository;
  private final DocumentRepository documentRepository;
  private final TranscriptionService transcriptionService;

  @Autowired
  public SimpleFileUploadService(
          FileStorageProperties fileStorageProperties,
          UserRepository userRepository,
          SpaceRepository spaceRepository,
          DocumentRepository documentRepository,
          TranscriptionService transcriptionService) {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    this.spaceRepository = spaceRepository;
    this.documentRepository = documentRepository;
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
            rootLocation.resolve(Paths.get(user.getUsername())).resolve(Paths.get(space.getName())).normalize().toAbsolutePath();

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
                    .resolve(space.getName())
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
      documentRepository.save(document);

      logger.info(
              "Created new document for file '" + path.getFileName().toString() + "' in space(" + space.getName()
                      + ") of user (" + space.getUser().getUsername() + ")");
    } else {
      logger.info(
              "File '" + path.getFileName().toString() + "' already exists, overriding the file content.");
    }
  }

  @Override
  public Path load(String filename, String spaceName, String userName) {
    return rootLocation.resolve(userName).resolve(spaceName).resolve(filename).normalize().toAbsolutePath();
  }

  @Override
  public Resource loadAsResource(Space space, String fileName) {
    String userName = space.getUser().getUsername();
    Path filePath = load(fileName, space.getName(), userName);
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
    List<Document> list = space.getDocuments();
    Path filePath = load(fileName, space.getName(), space.getUser().getUsername());

    for (Document document: list) {
      if (document.getName().equals(fileName)) {
        documentRepository.delete(document);
        break;
      }
    }

    try {
      Files.delete(filePath);
      logger.info("File '" + fileName + "' has been successfully deleted by user(" +
              space.getUser().getUsername() + ") in space(" + space.getName() + ").");
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
}
