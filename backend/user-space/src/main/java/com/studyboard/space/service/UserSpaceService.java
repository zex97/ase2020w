package com.studyboard.space.service;

import com.studyboard.model.Document;
import com.studyboard.model.Space;
import com.studyboard.model.User;
import com.studyboard.repository.SpaceRepository;
import com.studyboard.repository.UserRepository;
import com.studyboard.space.exception.SpaceDoesNotExist;
import com.studyboard.space.exception.UserDoesNotExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSpaceService implements UserSpace {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Space> getUserSpaces(long userId) {
        User user = findUserById(userId);
        return user.getSpaces();
    }

    @Override
    public void addSpaceToUser(long userId, Space space) {
        User user = findUserById(userId);
        user.getSpaces().add(space);
        userRepository.save(user);
    }

    @Override
    public void removeSpaceFromUser(long userId, long spaceId) {
        User user = findUserById(userId);
        user.getSpaces().removeIf(s -> s.getId() == spaceId);
        userRepository.save(user);
    }

    @Override
    public List<Document> geAllDocumentsFromSpace(long spaceId) {
        Space space = findSpaceById(spaceId);
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

    private User findUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }
}
