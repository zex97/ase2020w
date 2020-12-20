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
import java.util.stream.Collectors;

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
    public void addSpace(Space space) {
        spaceRepository.save(space);
    }

    @Override
    public void removeSpace(long spaceId) {
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public Space updateSpaceName(Space space) {
        Space storedSpace = findSpaceById(space.getId());
        storedSpace.setName(space.getName());
        return spaceRepository.save(storedSpace);
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
        List<Document> documents = space.getDocuments().stream().filter(d -> d.getId() != documentId).collect(Collectors.toList());
        space.setDocuments(documents);
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
