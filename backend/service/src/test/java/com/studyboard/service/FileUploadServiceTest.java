package com.studyboard.service;

import com.studyboard.FileStorageProperties;
import com.studyboard.exception.FileStorageException;
import com.studyboard.exception.StorageFileNotFoundException;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.DocumentRepository;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.service.implementation.SimpleFileUploadService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceTest.class);
  private final String TEST_FILE_NAME = "test.mp3";
  private final String TEST_FILE_NAME_1 = "test1.pdf";
  private final String TEST_PDF_FILE_NAME = "test.pdf";
  private final long TEST_SPACE_ID = 1;
  private final String TEST_SPACE_NAME = "DummySpace";
  private final String TEST_USER_NAME = "DummyUser";
  private final Path TEST_PATH_ENDING = Path.of(TEST_USER_NAME, TEST_SPACE_NAME, TEST_FILE_NAME);
  private static final String TEST_CLASS_RESOURCE_PATH = "src/test/resources/";

  @Mock private SpaceRepository spaceRepository;

  @Mock private DocumentRepository documentRepository;

  @Mock private TranscriptionService transcriptionService;

  @Mock private FileStorageProperties fileStorageProperties;

  @InjectMocks private SimpleFileUploadService simpleFileUploadService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(fileStorageProperties.getLocation())
        .thenReturn(TEST_CLASS_RESOURCE_PATH + "testing/");
    simpleFileUploadService.init();
  }

  @AfterAll
  public static void cleanUp() {
    logger.info("Performing a cleanup of created resources...");
    try {
      FileSystemUtils.deleteRecursively(Path.of(TEST_CLASS_RESOURCE_PATH, "testing"));
    } catch (IOException e) {
      logger.error("Failed to clean-up after tests");
      e.printStackTrace();
    }
  }

  @Test
  public void loadCorrectFilePathGivenUserNameSpaceAndFileName() {
    Assertions.assertTrue(
        simpleFileUploadService
            .load(TEST_FILE_NAME, TEST_SPACE_NAME, TEST_USER_NAME)
            .endsWith(TEST_PATH_ENDING));
  }

  @Test
  public void checkIfUserFolderIsDeleted() {
    try {
      Path path =
          Files.createDirectories(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME));
      Assertions.assertTrue(Files.exists(path));
      simpleFileUploadService.deleteUserFolder(TEST_USER_NAME);
      Assertions.assertFalse(Files.exists(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void checkIfUserNonExistingFileCanBeDeleted_throwsException() {
    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);
    Mockito.when(spaceRepository.findSpaceById(TEST_SPACE_ID)).thenReturn(space);
    // Mockito.doNothing().when(documentRepository.delete());
    FileStorageException ex =
        Assertions.assertThrows(
            FileStorageException.class,
            () -> simpleFileUploadService.deleteUserFile(TEST_FILE_NAME, TEST_SPACE_ID));
    Assertions.assertTrue(ex.getMessage().endsWith("does not exist!"));
  }

  @Test
  public void checkIfUserFileIsDeleted() {
    try {
      Files.createDirectories(
          Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME, TEST_SPACE_NAME)
              .toAbsolutePath());
      File file =
          new File(
              Path.of(
                      TEST_CLASS_RESOURCE_PATH,
                      "testing",
                      TEST_USER_NAME,
                      TEST_SPACE_NAME,
                      TEST_FILE_NAME)
                  .toString());
      Assertions.assertTrue(file.createNewFile());
    } catch (IOException e) {
      e.printStackTrace();
    }

    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    List<Document> list = new ArrayList<>();
    Document doc = new Document();
    doc.setName(TEST_FILE_NAME);
    list.add(doc);
    space.setDocuments(list);
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);

    Mockito.when(spaceRepository.findSpaceById(TEST_SPACE_ID)).thenReturn(space);

    simpleFileUploadService.deleteUserFile(TEST_FILE_NAME, TEST_SPACE_ID);
    Assertions.assertFalse(
        Files.exists(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME, TEST_FILE_NAME)));
  }

  @Test
  public void checkIfFileIsStoredCorrectly() throws IOException {
    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);
    Mockito.when(spaceRepository.findSpaceById(TEST_SPACE_ID)).thenReturn(space);
    Files.createDirectories(
        Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME, TEST_SPACE_NAME)
            .toAbsolutePath());

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file", TEST_FILE_NAME, MediaType.APPLICATION_PDF_VALUE, "Hello World!".getBytes());

    try {
      CompletableFuture<String> result =
          simpleFileUploadService.storeAsync(
              mockMultipartFile.getOriginalFilename(), mockMultipartFile.getBytes(), TEST_SPACE_ID);

      Assertions.assertEquals(TEST_FILE_NAME, result.get());

      Assertions.assertTrue(
          Files.exists(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));

      Assertions.assertArrayEquals(
          "Hello World!".getBytes(StandardCharsets.UTF_8),
          Files.readAllBytes(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));
    } catch (IOException | ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    FileSystemUtils.deleteRecursively(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME));
  }

  @Test
  public void checkIfFileIsStoredCorrectlyWithADifferentDocument() throws IOException {
    Document document = new Document();
    document.setName(TEST_FILE_NAME + "1");
    document.setFilePath("some/file/path");
    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);
    space.setDocuments(Collections.singletonList(document));
    Mockito.when(spaceRepository.findSpaceById(TEST_SPACE_ID)).thenReturn(space);
    // Mockito.doNothing().when(documentRepository.delete());
    Files.createDirectories(
        Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME, TEST_SPACE_NAME)
            .toAbsolutePath());

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file", TEST_FILE_NAME, MediaType.APPLICATION_PDF_VALUE, "Hello World!".getBytes());

    try {
      CompletableFuture<String> result =
          simpleFileUploadService.storeAsync(
              mockMultipartFile.getOriginalFilename(), mockMultipartFile.getBytes(), TEST_SPACE_ID);

      Assertions.assertEquals(TEST_FILE_NAME, result.get());

      Assertions.assertTrue(
          Files.exists(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));

      Assertions.assertArrayEquals(
          "Hello World!".getBytes(StandardCharsets.UTF_8),
          Files.readAllBytes(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));
    } catch (IOException | ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    FileSystemUtils.deleteRecursively(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME));
  }

  @Test
  public void checkIfFileIsStoredCorrectlyWithExistingDocument() throws IOException {
    Document document = new Document();
    document.setName(TEST_FILE_NAME + "1");
    document.setFilePath(
        Path.of(
                TEST_CLASS_RESOURCE_PATH,
                "testing",
                TEST_USER_NAME,
                TEST_SPACE_NAME,
                TEST_FILE_NAME)
            .toAbsolutePath()
            .toString());
    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);
    space.setDocuments(Collections.singletonList(document));
    Mockito.when(spaceRepository.findSpaceById(TEST_SPACE_ID)).thenReturn(space);
    // Mockito.doNothing().when(documentRepository.delete());
    Files.createDirectories(
        Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME, TEST_SPACE_NAME)
            .toAbsolutePath());

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file", TEST_FILE_NAME, MediaType.APPLICATION_PDF_VALUE, "Hello World!".getBytes());

    try {
      CompletableFuture<String> result =
          simpleFileUploadService.storeAsync(
              mockMultipartFile.getOriginalFilename(), mockMultipartFile.getBytes(), TEST_SPACE_ID);

      Assertions.assertEquals(TEST_FILE_NAME, result.get());

      Assertions.assertTrue(
          Files.exists(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));

      Assertions.assertArrayEquals(
          "Hello World!".getBytes(StandardCharsets.UTF_8),
          Files.readAllBytes(
              Path.of(
                  TEST_CLASS_RESOURCE_PATH,
                  "testing",
                  TEST_USER_NAME,
                  TEST_SPACE_NAME,
                  TEST_FILE_NAME)));
    } catch (IOException | ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
    FileSystemUtils.deleteRecursively(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME));
  }

  @Test
  public void checkIfNonExistingFileCanBeLoaded_throwsException() {
    User user = new User();
    user.setUsername(TEST_USER_NAME);
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);

    StorageFileNotFoundException ex =
        Assertions.assertThrows(
            StorageFileNotFoundException.class,
            () -> simpleFileUploadService.loadAsResource(space, TEST_FILE_NAME));
    Assertions.assertTrue(ex.getMessage().endsWith("could not be read, or doesn't exist"));
  }

  @Test
  public void checkIfExistingFileLoadedAsResource() {
    try {
      Files.createDirectories(
          Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME+ "1", TEST_SPACE_NAME)
              .toAbsolutePath());
      Files.write(
          Path.of(
              TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME + "1", TEST_SPACE_NAME, TEST_FILE_NAME_1),
          "Hello World!".getBytes(StandardCharsets.UTF_8));

      Assertions.assertTrue(Files.exists(Path.of(
              TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME + "1", TEST_SPACE_NAME, TEST_FILE_NAME_1)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    User user = new User();
    user.setUsername(TEST_USER_NAME + "1");
    Space space = new Space();
    space.setName(TEST_SPACE_NAME);
    space.setUser(user);
    Resource resource = simpleFileUploadService.loadAsResource(space, TEST_FILE_NAME_1);
    try {
      Assertions.assertArrayEquals(
          "Hello World!".getBytes(StandardCharsets.UTF_8),
          Files.readAllBytes(Path.of(
                  TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME + "1", TEST_SPACE_NAME, TEST_FILE_NAME_1)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals(TEST_FILE_NAME_1, resource.getFilename());
    try {
      FileSystemUtils.deleteRecursively(Path.of(TEST_CLASS_RESOURCE_PATH, "testing", TEST_USER_NAME + "1"));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
