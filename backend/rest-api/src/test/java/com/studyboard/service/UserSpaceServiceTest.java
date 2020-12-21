package com.studyboard.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.exception.SpaceDoesNotExist;
import com.studyboard.space.service.SimpleUserSpaceService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserSpaceServiceTest {

    private static final User TEST_USER_1 = new User("testUsername1", "testPassword1", "user1@email.com", 2, "USER", true);

    @Mock
    private SpaceRepository spaceRepository;

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

        Mockito.when(spaceRepository.findSpaceById(SPACE_ID)).thenReturn(space);

        List<Document> response = userSpaceService.geAllDocumentsFromSpace(SPACE_ID);
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
            userSpaceService.geAllDocumentsFromSpace(SPACE_ID);
        });
    }

    @Test
    public void updateSpaceNameSuccessfully() {
        final int SPACE_ID = 1;
        final String SPACE_NAME = "Space Name";
        final String NEW_SPACE_NAME = "New Space Name";
        Space space = new Space();
        space.setId(SPACE_ID);
        space.setName(SPACE_NAME);

        Mockito.when(spaceRepository.findSpaceById(SPACE_ID)).thenReturn(space);
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
        final int SPACE_ID = 1;
        Document document = new Document();
        document.setId(SPACE_ID);
        Space space = new Space();
        space.setId(SPACE_ID);

        Mockito.when(spaceRepository.findSpaceById(SPACE_ID)).thenReturn(space);
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
        final int SPACE_ID = 1;
        final int DOCUMENT_ID = 1;
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        Space space = new Space();
        space.setId(SPACE_ID);
        space.setDocuments(Arrays.asList(new Document[]{document}));

        Mockito.when(spaceRepository.findSpaceById(SPACE_ID)).thenReturn(space);
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
}
