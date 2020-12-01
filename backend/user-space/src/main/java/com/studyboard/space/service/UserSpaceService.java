package com.studyboard.space.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;

import java.util.List;

public interface UserSpaceService {

    /**
     * Get all spaces belonging to the user with the specified username.
     *
     * @param username of the user
     * @return all spaces user created
     */
    List<Space> getUserSpaces(String username);

    /**
     * Create a space
     *
     * @param username of the user creating the space
     * @param space    to be created
     */
    void addSpaceToUser(String username, Space space);

    /**
     * Delete a space
     *
     * @param username of the user the space belongs to
     * @param spaceId  of the space to be created
     */
    void removeSpaceFromUser(String username, long spaceId);

    /**
     * Update a single space
     *
     * @param username of the user who created the deck
     * @param space    - with the information to be updated
     * @return updated space
     */
    public Space updateSpaceName(String username, Space space);

    List<Document> geAllDocumentsFromSpace(long spaceId);

    void addDocumentToSpace(long spaceId, Document document);

    void removeDocumentFromSpace(long spaceId, long documentId);
}
