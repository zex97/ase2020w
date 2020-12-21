package com.studyboard.space.service;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.exception.SpaceDoesNotExist;
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
        Space space = spaceRepository.findSpaceById(spaceId);
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
    public List<Document> geAllDocumentsFromSpace(long spaceId) {
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

    private Space findSpaceById(long spaceId) {
        Space space = spaceRepository.findSpaceById(spaceId);
        if (space == null) {
            throw new SpaceDoesNotExist();
        }
        return space;
    }

    private User findUserByUsername(String username) {
        User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }
}
