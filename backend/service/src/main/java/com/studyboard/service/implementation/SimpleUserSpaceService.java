package com.studyboard.service.implementation;

import com.studyboard.exception.DocumentDoesNotExistException;
import com.studyboard.exception.TagDoesNotExistException;
import com.studyboard.repository.DocumentRepository;
import com.studyboard.service.UserSpaceService;
import com.studyboard.exception.SpaceDoesNotExist;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** Service used to manage user spaces. Performs space creation, getting and adding/removing documents to/from it */
@Service
public class SimpleUserSpaceService implements UserSpaceService {

    private final Logger logger = LoggerFactory.getLogger(UserSpaceService.class);

    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public List<Space> getUserSpaces(String username) {
        logger.info("Getting all user spaces for user with username " + username);
        return spaceRepository.findByUserUsername(username);
    }

    @Override
    public void addSpace(Space space) {
        logger.info("Created new user space with name " + space.getName());
        spaceRepository.save(space);
    }

    @Override
    public void removeSpace(long spaceId) {
        Space space = findSpaceById(spaceId);
        logger.info("Delete space with name " + space.getName());
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public Space updateSpaceName(Space space) {
        Space storedSpace = findSpaceById(space.getId());
        logger.info("Changed the space name: from "
                + storedSpace.getName() + " to: "
                + space.getName());
        storedSpace.setName(space.getName());
        return spaceRepository.save(storedSpace);
    }

    @Override
    public List<Document> getAllDocumentsFromSpace(long spaceId) {
        Space space = findSpaceById(spaceId);
        logger.info("Getting all documents of space with name " + space.getName());
        return space.getDocuments();
    }

    @Override
    public void addDocumentToSpace(long spaceId, Document document) {
        Space space = findSpaceById(spaceId);
        space.getDocuments().add(document);
        logger.info("Add document with name " + document.getName() + " to space with name " + space.getName());
        spaceRepository.save(space);
    }

    @Override
    public void removeDocumentFromSpace(long spaceId, long documentId) {
        Space space = findSpaceById(spaceId);
        List<Document> documents = space.getDocuments().stream().filter(d -> d.getId() != documentId).collect(Collectors.toList());
        space.setDocuments(documents);
        logger.info("Remove document from space with name " + space.getName());
        spaceRepository.save(space);
    }

    @Override
    public void addTagToDocument(long documentId, String tag) {
        Document document = findDocumentById(documentId);
        document.getTags().add(tag);
        logger.info("Add tag [{}] to document with name {}.", tag, document.getName());
        documentRepository.save(document);
    }

    @Override
    public void removeTagFromDocument(long documentId, String tag) {
        Document document = findDocumentById(documentId);

        if(!document.getTags().contains(tag)){
            throw new TagDoesNotExistException();
        }

        document.getTags().remove(tag);
        logger.info("Remove tag [{}] from document with name {}.", tag, document.getName());
        documentRepository.save(document);
    }

    private Space findSpaceById(long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(SpaceDoesNotExist::new);
        return space;
    }

    private Document findDocumentById(long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(DocumentDoesNotExistException::new);
        return document;
    }
}
