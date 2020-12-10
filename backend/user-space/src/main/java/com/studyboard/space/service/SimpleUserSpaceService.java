package com.studyboard.space.service;

import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.exception.SpaceDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleUserSpaceService implements UserSpaceService {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Space> getUserSpaces(String username) {
        return spaceRepository.findByUserUsername(username);
    }

    @Override
    public void addSpaceToUser(String username, Space space) {
        spaceRepository.save(space);
    }

    @Override
    public void removeSpaceFromUser(String username, long spaceId) {
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public Space updateSpaceName(String username, Space space) {
        Space storedSpace = findSpaceById(space.getId());
        storedSpace.setName(space.getName());
        return spaceRepository.save(storedSpace);
    }

    @Override
    public List<Document> geAllDocumentsFromSpace(long spaceId) {
        Space space = findSpaceById(spaceId);
        for (Document d : space.getDocuments()) {
      System.out.println(">>>" + d.getFilePath());
        }
        return space.getDocuments();
    }

    @Override
    public void addDocumentToSpace(long spaceId, Document document) {
        Space space = findSpaceById(spaceId);
        space.getDocuments().add(document);
        spaceRepository.save(space);
    }

    @Override
    public void removeDocumentFromSpace(long spaceId, long documentId) {
        Space space = findSpaceById(spaceId);
        space.getDocuments().removeIf(d -> d.getId() == documentId);
        spaceRepository.save(space);
    }

    private Space findSpaceById(long spaceId) {
        Space space = spaceRepository.findSpaceById(spaceId);
        if (space == null) {
            throw new SpaceDoesNotExist();
        }
        return space;
    }

    private User findUserById(String username) {
        User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }
}
