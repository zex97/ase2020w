package com.studyboard.rest;

import com.studyboard.model.Space;
import com.studyboard.space.service.UserSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/space")
public class UserSpaceController {
    @Autowired
    private UserSpaceService service;

    @RequestMapping(value="/{userId}", method = RequestMethod.GET, produces = "application/json")
    public List<Space> getUserSpaces(@PathVariable(name = "userId") long userId){
        return service.getUserSpaces(userId);
    }

    @RequestMapping(value="/{userId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addUserSpace(@PathVariable(name = "userId") long userId, @RequestBody Space space){
        service.addSpaceToUser(userId, space);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/{userId}/{spaceId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity deleteUserSpace(@PathVariable(name = "userId") long userId, @PathVariable(name = "spaceId") long spaceId){
        service.removeSpaceFromUser(userId, spaceId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/{userId}/{spaceId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getAllDocuments(@PathVariable(name = "userId") long userId, @PathVariable(name = "spaceId") long spaceId){
        return ResponseEntity.ok().body(service.geAllDocumentsFromSpace(spaceId));
    }
}
