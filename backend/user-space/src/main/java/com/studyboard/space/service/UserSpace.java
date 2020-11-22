package com.studyboard.space.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;

import java.util.List;

public interface UserSpace {
    List<Space> getUserSpaces(long userId);
    void addSpaceToUser(long userId, Space space);
    void removeSpaceFromUser(long userId, long spaceId);
    List<Document> geAllDocumentsFromSpace(long spaceId);
    void addDocumentToSpace(long spaceId, Document document);
    void removeDocumentFromSpace(long spaceId, long documentId);
}
