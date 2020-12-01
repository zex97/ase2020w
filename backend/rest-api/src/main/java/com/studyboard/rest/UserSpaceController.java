package com.studyboard.rest;

import com.studyboard.model.Space;
import com.studyboard.space.service.UserSpaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/space")
public class UserSpaceController {
    @Autowired
    private UserSpaceService service;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Get space associated with specific user.", authorizations = {@Authorization(value = "apiKey")})
    public List<Space> getUserSpaces(@PathVariable(name = "userId") long userId) {
        return service.getUserSpaces(userId);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Add space to specific user.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity addUserSpace(
            @PathVariable(name = "userId") long userId, @RequestBody Space space) {
        service.addSpaceToUser(userId, space);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{userId}/{spaceId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(value = "Delete a specific user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteUserSpace(
            @PathVariable(name = "userId") long userId, @PathVariable(name = "spaceId") long spaceId) {
        service.removeSpaceFromUser(userId, spaceId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{userId}/{spaceId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "Get all documents associated with specific user and space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity getAllDocuments(
            @PathVariable(name = "userId") long userId, @PathVariable(name = "spaceId") long spaceId) {
        return ResponseEntity.ok().body(service.geAllDocumentsFromSpace(spaceId));
    }

    @RequestMapping(
            value = "/{userId}/{spaceId}/{documentId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "Delete specific document from user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteDocumentFromSpace(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "spaceId") long spaceId,
            @PathVariable(name = "documentId") long documentId) {
        service.removeDocumentFromSpace(spaceId, documentId);
        return ResponseEntity.ok().build();
    }
}
