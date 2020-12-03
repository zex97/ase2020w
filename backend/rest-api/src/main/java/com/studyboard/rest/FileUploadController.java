package com.studyboard.rest;

import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.StorageFileNotFoundException;
import com.studyboard.uploader.service.FileUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/upload")
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileUploadController {

  @Autowired FileUploaderService fileUploaderService;

  /** @param file accepts files up to 20MB (can be changed in application.properties) */
  @RequestMapping(
      value = "/single-file/{userId}",
      method = RequestMethod.POST,
      produces = "application/json")
  public ResponseEntity<String> handleFileUpload(
          @RequestParam("file") MultipartFile file, @PathVariable long userId) {
    fileUploaderService.store(file, userId);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(file.getOriginalFilename());
  }

  @RequestMapping(
      value = "/file/{fileName}/{userId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Resource> getFile(
      @PathVariable(name = "fileName") String fileName, @PathVariable long userId) {
    Resource file = fileUploaderService.loadAsResource(fileName, userId);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @RequestMapping(
      value = "/multiple-files/{userId}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public List<ResponseEntity<String>> uploadMultipleFiles(
      @RequestParam("file") MultipartFile[] files, @PathVariable long userId) {
    return Arrays.stream(files)
            .map(file -> handleFileUpload(file, userId))
            .collect(Collectors.toList());
  }

  @RequestMapping(
      value = "/delete-file/{fileName}/{userId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  public void deleteUserUpload(@PathVariable(value = "fileName") String fileName, @PathVariable long userId) {
    fileUploaderService.deleteUserFile(fileName, userId);
  }

  @RequestMapping(
      value = "/delete-folder/{userId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  public void deleteUserFolder(@PathVariable long userId) {
    fileUploaderService.deleteUserFolder(userId);
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageException(StorageFileNotFoundException e) {
    return ResponseEntity.noContent().build();
  }
}
