package com.studyboard.rest;

import com.studyboard.dto.SpaceDTO;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
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

import javax.swing.*;
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
      value = "/single-file/{space}",
      method = RequestMethod.POST,
      produces = "application/json")
  public ResponseEntity handleFileUpload(
          @RequestParam("file") MultipartFile file, @PathVariable long space) {
    System.out.println(file.getName() + " " + space);
    fileUploaderService.store(file, space);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/file/{fileName}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public ResponseEntity<Resource> getFile(
      @RequestParam(name = "fileName") SpaceDTO spaceDTO, @PathVariable String fileName) {
    Resource file = fileUploaderService.loadAsResource(spaceDTO.toSpace(), fileName);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @RequestMapping(
      value = "/delete-file/{fileName}}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  public void deleteUserUpload(@PathVariable(value = "fileName") String fileName, @RequestBody SpaceDTO spaceDTO) {
    fileUploaderService.deleteUserFile(fileName, spaceDTO.toSpace());
  }

  @RequestMapping(
      value = "/delete-folder/{userName}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  public void deleteUserFolder(@PathVariable String userName) {
    fileUploaderService.deleteUserFolder(userName);
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageException(StorageFileNotFoundException e) {
    return ResponseEntity.noContent().build();
  }
}
