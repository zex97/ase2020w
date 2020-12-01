package com.studyboard.space.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;

import java.util.List;

public interface UserSpaceService {
    List<Space> getUserSpaces(String username);

    void addSpaceToUser(String username, Space space);

    void removeSpaceFromUser(String username, long spaceId);

    List<Document> geAllDocumentsFromSpace(long spaceId);

    void addDocumentToSpace(long spaceId, Document document);

    void removeDocumentFromSpace(long spaceId, long documentId);
}
