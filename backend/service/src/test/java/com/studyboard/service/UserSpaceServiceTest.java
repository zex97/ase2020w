package com.studyboard.service;

import com.studyboard.exception.DocumentDoesNotExistException;
import com.studyboard.exception.IllegalTagException;
import com.studyboard.exception.SpaceDoesNotExist;
import com.studyboard.exception.TagDoesNotExistException;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.DocumentRepository;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.service.implementation.SimpleUserSpaceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserSpaceServiceTest {

    private static final User TEST_USER_1 = new User("testUsername1", "testPassword1", "user1@email.com", 2, "USER", true);

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    DocumentRepository documentRepository;

    @InjectMocks
    private SimpleUserSpaceService userSpaceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllUserSpacesByUsernameSuccessfully() {
        User user = new User(TEST_USER_1);
        Space space = new Space();
        space.setUser(user);

        Mockito.when(spaceRepository.findByUserUsername(user.getUsername())).thenReturn(Collections.singletonList(space));

        List<Space> response = userSpaceService.getUserSpaces(user.getUsername());
        Space storedSpace = response.get(0);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(space.getUser().getId(), storedSpace.getUser().getId());
    }

    @Test
    public void getAnyUserSpacesByUsernameSuccessfully() {
        User user = new User(TEST_USER_1);

        List<Space> response = userSpaceService.getUserSpaces(user.getUsername());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());
    }


    @Test
    public void getAllDocumentsFromUserSpaceByIdSuccessfully() {
        final long SPACE_ID = 1;
        final long DOCUMENT_1_ID = 1;
        final long DOCUMENT_2_ID = 2;
        Space space = new Space();
        space.setId(SPACE_ID);
        Document doc1 = new Document();
        doc1.setId(DOCUMENT_1_ID);
        Document doc2 = new Document();
        doc2.setId(DOCUMENT_2_ID);
        space.setDocuments(Arrays.asList(new Document[]{doc1, doc2}));

        Mockito.when(spaceRepository.findById(SPACE_ID)).thenReturn(Optional.of(space));

        List<Document> response = userSpaceService.getAllDocumentsFromSpace(SPACE_ID);
        Document document1 = response.get(0);
        Document document2 = response.get(1);

        Assertions.assertNotNull(document1);
        Assertions.assertEquals(doc1.getId(), document1.getId());
        Assertions.assertNotNull(document2);
        Assertions.assertEquals(doc2.getId(), document2.getId());
    }

    @Test
    public void getDocumentsFromSpaceThatDoesNotExist_ThrowsSpaceDoesNotExist() {
        final long SPACE_ID = 1;

        Assertions.assertThrows(SpaceDoesNotExist.class, () -> {
            userSpaceService.getAllDocumentsFromSpace(SPACE_ID);
        });
    }

    @Test
    public void updateSpaceNameSuccessfully() {
        final long SPACE_ID = 1;
        final String SPACE_NAME = "Space Name";
        final String NEW_SPACE_NAME = "New Space Name";
        Space space = new Space();
        space.setId(SPACE_ID);
        space.setName(SPACE_NAME);

        Mockito.when(spaceRepository.findById(SPACE_ID)).thenReturn(Optional.of(space));
        Mockito.when(spaceRepository.save(space)).thenReturn(space);

        space.setName(NEW_SPACE_NAME);
        Space storedSpace = userSpaceService.updateSpaceName(space);

        Assertions.assertNotNull(storedSpace);
        Assertions.assertEquals(NEW_SPACE_NAME, storedSpace.getName());
    }

    @Test
    public void updateSpaceNameThatDoesNotExists_ThrowsSpaceDoesNotExist() {
        final int SPACE_ID = 1;
        Space space = new Space();
        space.setId(SPACE_ID);

        Assertions.assertThrows(SpaceDoesNotExist.class, () -> {
            userSpaceService.updateSpaceName(space);
        });
    }


    @Test
    public void addDocumentToSpaceSuccessfully() {
        final long SPACE_ID = 1;
        Document document = new Document();
        document.setId(SPACE_ID);
        Space space = new Space();
        space.setId(SPACE_ID);

        Mockito.when(spaceRepository.findById(SPACE_ID)).thenReturn(Optional.of(space));
        Mockito.when(spaceRepository.save(space)).thenReturn(space);

        userSpaceService.addDocumentToSpace(SPACE_ID, document);

        Assertions.assertEquals(1, space.getDocuments().size());
    }

    @Test
    public void addDocumentToSpaceThatDoesNotExists_ThrowsSpaceDoesNotExist() {
        final int SPACE_ID = 1;
        Document document = new Document();
        document.setId(SPACE_ID);

        Assertions.assertThrows(SpaceDoesNotExist.class, () -> {
            userSpaceService.addDocumentToSpace(SPACE_ID, document);
        });
    }

    @Test
    public void removeDocumentFromSpaceSuccessfully() {
        final long SPACE_ID = 1;
        final int DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        Space space = new Space();
        space.setId(SPACE_ID);
        space.setDocuments(Arrays.asList(new Document[]{document}));

        Mockito.when(spaceRepository.findById(SPACE_ID)).thenReturn(Optional.of(space));
        Mockito.when(spaceRepository.save(space)).thenReturn(space);

        userSpaceService.removeDocumentFromSpace(SPACE_ID, DOCUMENT_ID);

        Assertions.assertEquals(0, space.getDocuments().size());
    }


    @Test
    public void removeDocumentFromSpaceThatDoesNotExists_ThrowsSpaceDoesNotExist() {
        final int SPACE_ID = 1;
        final int DOCUMENT_ID = 1;

        Assertions.assertThrows(SpaceDoesNotExist.class, () -> {
            userSpaceService.removeDocumentFromSpace(SPACE_ID, DOCUMENT_ID);
        });
    }

    @Test
    public void addValidTagToExistingDocumentSuccessfully(){
        final String TAG = "test-tag";
        final long DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        Mockito.when(documentRepository.save(document)).thenReturn(document);

        userSpaceService.addTagToDocument(DOCUMENT_ID, TAG);

        Assertions.assertTrue(document.getTags().contains(TAG));
    }

    @Test
    public void addValidTagToUnknownDocument_throwsDocumentDoesNotExistException(){
        final String TAG = "test-tag";
        final long DOCUMENT_ID = 1;

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(DocumentDoesNotExistException.class, () -> {
            userSpaceService.addTagToDocument(DOCUMENT_ID, TAG);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n"})
    public void addInvalidTagToExistingDocument_throwsIllegalTagException(final String TAG){
        final long DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        Assertions.assertThrows(IllegalTagException.class, () -> {
            userSpaceService.addTagToDocument(DOCUMENT_ID, TAG);
        });
    }
    @Test
    public void removeExistingTagFromExistingDocumentSuccessfully(){
        final String TAG = "test-tag";
        final long DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.getTags().add(TAG);

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        Mockito.when(documentRepository.save(document)).thenReturn(document);

        userSpaceService.removeTagFromDocument(DOCUMENT_ID, TAG);

        Assertions.assertTrue(document.getTags().isEmpty());
    }

    @Test
    public void removeUnknownTagFromValidDocument_throwsTagDoesNotExistException(){
        final String TAG = "tag";
        final long DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        Assertions.assertThrows(TagDoesNotExistException.class, () -> {
            userSpaceService.removeTagFromDocument(DOCUMENT_ID, TAG);
        });
    }

    @Test
    public void removeAnyTagFromUnknownDocument_throwsDocumentDoesNotExistException(){
        final String TAG = "test-tag";
        final long DOCUMENT_ID = 1;

        Mockito.when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(DocumentDoesNotExistException.class, () -> {
            userSpaceService.removeTagFromDocument(DOCUMENT_ID, TAG);
        });
    }


}
