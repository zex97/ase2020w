package com.studyboard.rest;

import com.studyboard.model.Space;
import com.studyboard.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api/space")
public class SpaceController {
    @Autowired
    private SpaceService service;

    @RequestMapping(value="/getAll", method = RequestMethod.GET, produces = "application/json")
    public List<Space> getAllSpaces(){
        return service.getAllSpaces();
    }
}
