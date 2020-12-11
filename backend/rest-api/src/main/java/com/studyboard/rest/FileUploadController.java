package com.studyboard.rest;

import com.studyboard.dto.SpaceDTO;
import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.StorageFileNotFoundException;
import com.studyboard.uploader.service.FileUploaderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
  @ApiOperation(
      value = "Save a user specified file.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity handleFileUpload(
      @RequestParam("file") MultipartFile file, @PathVariable long space) {
    System.out.println(file.getOriginalFilename() + " " + space);
    fileUploaderService.store(file, space);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/file/{fileName}",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @ApiOperation(
      value = "Fetch a specific file.",
      authorizations = {@Authorization(value = "apiKey")})
  public ResponseEntity<Resource> getFile(
      @RequestParam(name = "fileName") SpaceDTO spaceDTO, @PathVariable String fileName) {
    Resource file = fileUploaderService.loadAsResource(spaceDTO.toSpace(), fileName);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @RequestMapping(
      value = "/delete-file/{spaceId}/{fileName}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ApiOperation(
      value = "Delete a specific user file in space.",
      authorizations = {@Authorization(value = "apiKey")})
  @CrossOrigin
  public ResponseEntity deleteUserUpload(
      @PathVariable(value = "fileName") String fileName,
      @PathVariable(value = "spaceId") long spaceId) {
    System.out.println("Filename: " + fileName + " space" + spaceId);
    fileUploaderService.deleteUserFile(fileName, spaceId);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      value = "/delete-folder/{userName}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ApiOperation(
      value = "Delete user folder and all files in it.",
      authorizations = {@Authorization(value = "apiKey")})
  public void deleteUserFolder(@PathVariable String userName) {
    fileUploaderService.deleteUserFolder(userName);
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageException(StorageFileNotFoundException e) {
    return ResponseEntity.noContent().build();
  }
}
