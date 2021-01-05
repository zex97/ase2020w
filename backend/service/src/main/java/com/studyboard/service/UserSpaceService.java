package com.studyboard.service;

import com.studyboard.model.Deck;
import com.studyboard.model.Document;
import com.studyboard.model.Flashcard;
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
     * @param space    to be created
     */
    void addSpace(Space space);

    /**
     * Delete a space
     *
     * @param spaceId  of the space to be created
     */
    void removeSpace(long spaceId);

    /**
     * Update a single space
     *
     * @param space - with the information to be updated
     * @return updated space
     */
    public Space updateSpaceName(Space space);

    /**
     * Returns all documents for a single space
     *
     * @param spaceId id of the space
     * @return list of documents that belong to space with id @param spaceId
     */
    List<Document> getAllDocumentsFromSpace(long spaceId);

    /**
     * Adds a single document to a space
     *
     * @param spaceId id of the space
     * @param document that is added to the space
     */
    void addDocumentToSpace(long spaceId, Document document);

    /**
     * Removes a single document from space
     *
     * @param spaceId id of the space
     * @param documentId id of the document that is deleted
     */
    void removeDocumentFromSpace(long spaceId, long documentId);

    /**
<<<<<<< HEAD
     * Adds a tag to a document
     *
     * @param documentId id of the document
     * @param tag new tag to be added
     */
    void addTagToDocument(long documentId, String tag);

    /**
     * Removes a tag from a document.
     * Throws an exception if either document or tag does not exist.
     *
     * @param documentId id of the document
     * @param tag new tag to be added
     */
    void removeTagFromDocument(long documentId, String tag);

    /**
     * Edit a transcription as part of document entity
     *
     * @param document entity that needs to be edited
     */
    void editTranscription(Document document);

    /**
     * Find a space containing the search parameter in the name
     *
     * @param username of the user searching for own spaces
     * @param searchParam to look for in the space's name
     * @return all spaces containing searchParam in the name
     */
    List<Space> getSpacesByName(String username, String searchParam);
}
