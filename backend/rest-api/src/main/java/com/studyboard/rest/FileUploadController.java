package com.studyboard.rest;

import com.studyboard.uploader.FileStorageProperties;
import com.studyboard.uploader.exception.FileNotFoundExceptionFile;
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

  /** @param file accepts files up to 10MB (can be changed in application.properties) */
  @RequestMapping(value = "/new")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
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

  @RequestMapping(value = "/multiple-files", method = RequestMethod.GET)
  @ResponseBody
  public List<ResponseEntity<String>> uploadMultipleFiles(
      @RequestParam("files") MultipartFile[] files) {
    return Arrays.stream(files).map(this::handleFileUpload).collect(Collectors.toList());
  }

  @RequestMapping(value = "/delete-file/{fileId}", method = RequestMethod.DELETE, produces = "application/json")
  public void deleteUserUpload(@PathVariable(value = "fileId") String fileId) {
    fileUploaderService.deleteUserFile(fileId);
  }

  @ExceptionHandler(FileNotFoundExceptionFile.class)
  public ResponseEntity<?> handleStorageException(FileNotFoundExceptionFile e) {
    return ResponseEntity.notFound().build();
  }
}
