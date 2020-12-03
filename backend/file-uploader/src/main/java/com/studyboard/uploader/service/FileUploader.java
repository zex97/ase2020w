package com.studyboard.uploader.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploader {

  void init();

  String store(MultipartFile file, long userId);

  Path load(String fileName, long userId);

  Resource loadAsResource(String fileName, long usedId);

  void deleteUserFile(String fileName, long userId);

  void deleteUserFolder(long userId);
}
