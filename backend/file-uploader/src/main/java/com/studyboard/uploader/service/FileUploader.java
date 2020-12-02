package com.studyboard.uploader.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploader {

  void init();

  String store(MultipartFile file, String userId);

  Path load(String fileName, String userId);

  Resource loadAsResource(String fileName, String usedId);

  void deleteUserFile(String fileName, String userId);

  void deleteUserFolder(String userId);
}
