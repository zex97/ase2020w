package com.studyboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
public class FileStorageProperties {

  private String location;
  @Value("${storage.file.types}")
  private String[] fileTypes;

  @Bean
  public FileStorageProperties storageProperties() {
    return this;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String[] getFileTypes() {
    return fileTypes;
  }

  public void setFileTypes(String[] fileTypes) {
    this.fileTypes = fileTypes;
  }
}
