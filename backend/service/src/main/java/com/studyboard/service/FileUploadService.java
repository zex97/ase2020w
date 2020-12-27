package com.studyboard.service;

import com.studyboard.model.Space;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface FileUploadService {

  /**
   * On the start of the application create a folder used as application storage
   */
  void init();

  /**
   * Stores file in the storage and creates a matching document for this file
   * If a user file does not exist, it is generated
   *
   * @param file object that needs to be saved
   * @param spaceId id of the space to which the object belongs to
   * @return name of the file that was stored
   */
  String store(MultipartFile file, long spaceId);

  /**
   * Thread safe version of the store function
   * Separates file into name and content and saves them in-memory to
   * avoid loss of multipart-file resources
   *
   * @param fileName name of the file to be uploaded
   * @param content of the file in byte array format
   * @param spaceId id of the space to which it belongs
   * @return name of the file that was saved
   */
  CompletableFuture<String> storeAsync(String fileName, byte[] content, long spaceId);

  /**
   * Generates a path where the file is saved
   *
   * @param fileName name of the file that is requested
   * @param spaceName name of the space in which the file is
   * @param username name of the user requesting the file
   * @return Path object for the @param fileName file
   */
  Path load(String fileName, String spaceName, String username);

  /**
   * Loads the file as resource for a space
   *
   * @param space to which the file belongs to
   * @param fileName name of the requested file
   * @return Resource object of the requested file
   */
  Resource loadAsResource(Space space, String fileName);

  /**
   * Deletes the user file
   *
   * @param fileName name of the file that needs to be deleted
   * @param spaceId space for which this file is deleted
   */
  void deleteUserFile(String fileName, long spaceId);

  /**
   * Deletes the user folder with all the files belonging to the user
   *
   * @param userId id of the user that is being deleted
   */
  void deleteUserFolder(String userId);
}
