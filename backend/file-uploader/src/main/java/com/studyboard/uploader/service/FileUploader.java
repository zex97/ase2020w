package com.studyboard.uploader.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileUploader {

  void init();

  String store(MultipartFile file);

  Stream<Resource> loadAll();

  Path load(String filename);

  Resource loadAsResource(String filename);

  void deleteUserFile();
}
