package com.studyboard.rest;

import com.studyboard.uploader.StorageProperties;
import com.studyboard.uploader.exception.StorageException;
import com.studyboard.uploader.service.FileUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/upload")
@EnableConfigurationProperties(StorageProperties.class)
public class FileUploadController {

  @Autowired FileUploaderService fileUploaderService;

  /** @param file accepts files up to 10MB (can be changed in application.properties) */
  @RequestMapping(value = "/new")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    String message = "";
    fileUploaderService.store(file);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(file.getOriginalFilename());
  }

  @RequestMapping(value = "/file/{fileName}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Resource> getFile(@PathVariable(name = "fileName") String fileName) {
    Resource file = fileUploaderService.loadAsResource(fileName);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }

  @ExceptionHandler
  public ResponseEntity<?> handleStorageException(StorageException e) {
    return ResponseEntity.notFound().build();
  }
}
