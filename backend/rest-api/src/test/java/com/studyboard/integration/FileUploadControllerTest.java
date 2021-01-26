package com.studyboard.integration;

import com.studyboard.dto.SpaceDTO;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileUploadControllerTest extends BaseIntegrationTest {

  private static final String FILE_UPLOAD_ENDPOINT = "/api/upload";
  private static final String USER_ENDPOINT = "/api/user";
  private static final String SPACE_ENDPOINT = "/api/space";

  private static Space TEST_SPACE;
  private static Space TEST_SPACE_1;
  private static Document TEST_DOCUMENT;
  private static Document TEST_DOCUMENT_1;
  private static final String TEST_FILE_NAME = "test-file-name.pdf";
  private static final String TEST_FILE_NAME_GET = "test-file-name-get.pdf";
  private static final String TEST_FILE_NAME_NON_EXITING = "test-file-name-non-existing.pdf";
  private static final String TEST_FILE_NAME_DELETE = "test-file-name-delete.pdf";

  private static final User TEST_USER =
      new User("testUsername12", "testPassword12", "user21@email.com", 2, "USER", true);

  @BeforeEach
  void setUp() throws Exception {
    User user = new User(TEST_USER);
    String requestJson = convertObjectToStringForJson(user);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(USER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        //                .andDo(print())
        .andExpect(status().isOk());

    ResultActions resultActionsUser =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(USER_ENDPOINT).accept(MediaType.APPLICATION_JSON))
            //                        .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$[0].loginAttempts")
                    .value(TEST_USER.getLoginAttempts()));

    String responseString = resultActionsUser.andReturn().getResponse().getContentAsString();
    User[] responseArray = mapper.readValue(responseString, User[].class);
    TEST_SPACE = new Space("test-space-1-1", LocalDate.now(), responseArray[0]);
    // TEST_SPACE.setUser(TEST_USER);
    TEST_SPACE_1 = new Space("test-space-1-2", LocalDate.now(), responseArray[0]);
    TEST_SPACE_1.setId(1);
    TEST_DOCUMENT = new Document();
    TEST_DOCUMENT.setName("test-document");
    TEST_DOCUMENT.setTranscription("test-transcription");
    TEST_DOCUMENT.setSpace(TEST_SPACE);
    TEST_DOCUMENT.setNeedsTranscription(false);
    TEST_DOCUMENT_1 = new Document();
    TEST_DOCUMENT_1.setName("test-document-1");
    TEST_DOCUMENT_1.setTranscription("test-transcription-1");
    TEST_DOCUMENT_1.setSpace(TEST_SPACE_1);
    TEST_DOCUMENT_1.setNeedsTranscription(true);
  }

  @AfterEach
  void tearDown() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "document");
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "space");
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "sb_user", "user_roles");
  }

  @AfterAll
  void cleanUp() {
    System.out.println("Perform clean-up after the tests...");
    try {
      FileSystemUtils.deleteRecursively(Path.of("uploadedFiles"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testUploadingOfNewValidFile() throws Exception {

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file", TEST_FILE_NAME, MediaType.APPLICATION_PDF_VALUE, "Hello World!!!".getBytes());

    SpaceDTO space1 = SpaceDTO.of(TEST_SPACE);
    String requestJson1 = convertObjectToStringForJson(space1);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(SPACE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson1))
        .andExpect(status().isOk());

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.multipart(FILE_UPLOAD_ENDPOINT + "/single-file/1", 1)
                .file(mockMultipartFile))
        // .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void testFetchingOfFiles() throws Exception {

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file",
            TEST_FILE_NAME_GET,
            MediaType.APPLICATION_PDF_VALUE,
            "Hello World!!!".getBytes());

    SpaceDTO space1 = SpaceDTO.of(TEST_SPACE_1);
    String requestJson1 = convertObjectToStringForJson(space1);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(SPACE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson1))
        .andExpect(status().isOk());
    // .andExpect(MockMvcResultMatchers.jsonPath(""));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.multipart(FILE_UPLOAD_ENDPOINT + "/single-file/1", 1)
                .file(mockMultipartFile)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    Files.createDirectories(Path.of("uploadedFiles", TEST_USER.getUsername(), "1"));
    Files.write(
        Path.of("uploadedFiles/" + TEST_USER.getUsername() + "/" + "1" + "/_" + TEST_FILE_NAME_GET),
        "Hello World!".getBytes(StandardCharsets.UTF_8));
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(FILE_UPLOAD_ENDPOINT + "/file/" + TEST_FILE_NAME_GET, 1)
                .content(requestJson1)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void testUploadingOfNewFileWithInvalidFileType_ThrowsException() throws Exception {

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file",
            TEST_FILE_NAME + "234",
            MediaType.APPLICATION_PDF_VALUE,
            "Hello World!!!".getBytes());

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.multipart(FILE_UPLOAD_ENDPOINT + "/single-file/1", 1)
                .file(mockMultipartFile))
        // .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void tryingToFetchNonExistingFile_ThrowsException() throws Exception {

    SpaceDTO space1 = SpaceDTO.of(TEST_SPACE);
    String requestJson1 = convertObjectToStringForJson(space1);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    FILE_UPLOAD_ENDPOINT + "/file/" + TEST_FILE_NAME_NON_EXITING, 1)
                .content(requestJson1)
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(1)
  void testDeleteFile() throws Exception {

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile(
            "file",
            TEST_FILE_NAME_DELETE,
            MediaType.APPLICATION_PDF_VALUE,
            "Hello World!!!".getBytes());

    Files.createDirectories(Path.of("uploadedFiles", TEST_USER.getUsername(), "2"));
    Files.write(
        Path.of(
            "uploadedFiles/" + TEST_USER.getUsername() + "/" + "2" + "/_" + TEST_FILE_NAME_DELETE),
        "Hello World!".getBytes(StandardCharsets.UTF_8));

    List<Document> list = Collections.singletonList(TEST_DOCUMENT);
    TEST_SPACE_1.setDocuments(list);
    SpaceDTO space1 = SpaceDTO.of(TEST_SPACE_1);
    String requestJson1 = convertObjectToStringForJson(space1);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(SPACE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson1))
        .andExpect(status().isOk());

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                FILE_UPLOAD_ENDPOINT + "/delete-file/" + "2/" + TEST_FILE_NAME_DELETE, 2))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @Order(6)
  void testDeletionOfUserSpaceFolder() throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                FILE_UPLOAD_ENDPOINT
                    + "/delete-space-folder/"
                    + TEST_USER.getUsername()
                    + "/"
                    + "1",
                2))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void testDeletionOfUserFolder() throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                FILE_UPLOAD_ENDPOINT + "/delete-folder/" + TEST_USER.getUsername(), 2))
        .andExpect(status().isOk());
  }
}
