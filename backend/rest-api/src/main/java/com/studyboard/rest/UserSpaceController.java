package com.studyboard.rest;

import com.studyboard.dto.SpaceDTO;
import com.studyboard.space.service.SimpleUserSpaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/space")
public class UserSpaceController {
    @Autowired
    private SimpleUserSpaceService service;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Get space associated with specific user.", authorizations = {@Authorization(value = "apiKey")})
    public List<SpaceDTO> getUserSpaces(@PathVariable(name = "username") String username) {
        return service.getUserSpaces(username).stream()
                .map(SpaceDTO::of)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Add space to specific user.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity addUserSpace(
            @PathVariable(name = "username") String username, @RequestBody SpaceDTO spaceDTO) {
        service.addSpaceToUser(username, spaceDTO.toSpace());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{username}/{spaceId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(value = "Delete a specific user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteUserSpace(
            @PathVariable(name = "username") String username, @PathVariable(name = "spaceId") long spaceId) {
        service.removeSpaceFromUser(username, spaceId);
        return ResponseEntity.ok().build();
    }

    /*@RequestMapping(
            value = "/{username}/{spaceId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "Get all documents associated with specific user and space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity getAllDocuments(
            @PathVariable(name = "username") String username, @PathVariable(name = "spaceId") long spaceId) {
        return ResponseEntity.ok().body(service.geAllDocumentsFromSpace(spaceId));
    }

    @RequestMapping(
            value = "/{username}/{spaceId}/{documentId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "Delete specific document from user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteDocumentFromSpace(
            @PathVariable(name = "username") String username,
            @PathVariable(name = "spaceId") long spaceId,
            @PathVariable(name = "documentId") long documentId) {
        service.removeDocumentFromSpace(spaceId, documentId);
        return ResponseEntity.ok().build();
    }*/
}
