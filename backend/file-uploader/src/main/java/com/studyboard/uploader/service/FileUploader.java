package com.studyboard.uploader.service;

import com.studyboard.model.Space;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploader {

  void init();

  String store(MultipartFile file, long spaceId);

  Path load(String fileName, String userId);

  Resource loadAsResource(String fileName, String usedId);

  void deleteUserFile(String fileName, Space space);

  void deleteUserFolder(String userId);
}
