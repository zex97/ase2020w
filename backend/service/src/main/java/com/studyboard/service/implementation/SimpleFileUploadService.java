package com.studyboard.service.implementation;

import com.studyboard.FileStorageProperties;
import com.studyboard.exception.FileStorageException;
import com.studyboard.exception.StorageFileNotFoundException;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.repository.DocumentRepository;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.service.FileUploadService;
import com.studyboard.service.TranscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Service used to manage user files. Performs file saving, loading, and deletion */
@Service
public class SimpleFileUploadService implements FileUploadService {

  private final Logger logger = LoggerFactory.getLogger(SimpleFileUploadService.class);
  private Path rootLocation;

  @Autowired private SpaceRepository spaceRepository;
  @Autowired private DocumentRepository documentRepository;
  @Autowired private TranscriptionService transcriptionService;
  @Autowired private FileStorageProperties fileStorageProperties;

  @Override
  @PostConstruct
  public void init() {
    this.rootLocation = Paths.get(fileStorageProperties.getLocation());
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new FileStorageException("Unable to initialize local storage");
    }
  }

  @Override
  @Async
  public CompletableFuture<String> storeAsync(String fileName, byte[] content, long spaceId) {
    Space space = spaceRepository.findSpaceById(spaceId);

    // user/space path
    Path spacePath = this.rootLocation
            .resolve(space.getUser().getUsername())
            .resolve(space.getName())
            .normalize().toAbsolutePath();

    // if user or space folder doesn't exist create one
    if(!Files.exists(spacePath)) {
      try {
        Files.createDirectories(spacePath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Path uploadFilePath =
        this.rootLocation
            .resolve(space.getUser().getUsername())
            .resolve(space.getName())
            .resolve(Paths.get(fileName))
            .normalize()
            .toAbsolutePath();

    try {
      Files.write(uploadFilePath, content);

    } catch (IOException e) {
      throw new FileStorageException("Failed to store file (" + fileName + ")!", e);
    }

    // reference the document of the file
    storeRefToNewDocument(space, uploadFilePath);

    return CompletableFuture.completedFuture(fileName);
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
      if (document.isNeedsTranscription()) {
        transcriptionService.transcribe(document);
      }
      documentRepository.save(document);

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
  public Path load(String filename, String spaceName, String userName) {
    return rootLocation
        .resolve(userName)
        .resolve(spaceName)
        .resolve(filename)
        .normalize()
        .toAbsolutePath();
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
        logger.debug(
            "Reading of file '"
                + fileName
                + "' stored on path "
                + filePath
                + " failed, resource doesn't exist");
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

    for (Document document : list) {
      if (document.getName().equals(fileName)) {
        documentRepository.delete(document);
        break;
      }
    }

    try {
      if (!Files.deleteIfExists(filePath)) {
        throw new FileStorageException(
            "File '"
                + fileName
                + "' on the path ("
                + filePath.toAbsolutePath().toString()
                + ") does not exist!");
      }
      logger.info(
          "File '"
              + fileName
              + "' has been successfully deleted by user("
              + space.getUser().getUsername()
              + ") in space("
              + space.getName()
              + ").");
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete file '" + fileName + "'");
    }
  }

  @Override
  public void deleteUserFolder(String userName) {
    FileSystemUtils.deleteRecursively(rootLocation.resolve(userName).toFile());
  }
}
