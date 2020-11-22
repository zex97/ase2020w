package com.studyboard.space.service;

import com.studyboard.model.Space;
import com.studyboard.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceService {
    @Autowired
    private SpaceRepository repository;

    public List<Space> getAllSpaces(){
        return repository.findAll();
    }
}
