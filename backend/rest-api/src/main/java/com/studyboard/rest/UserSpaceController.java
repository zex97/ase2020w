package com.studyboard.rest;

import com.studyboard.dto.DocumentDTO;
import com.studyboard.dto.SpaceDTO;
import com.studyboard.service.implementation.SimpleUserSpaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
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

    @RequestMapping(value = "/{username}/{searchParam}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(
            value = "Get all spaces containing the search parameter in the name.",
            authorizations = {@Authorization(value = "apiKey")})
    public List<SpaceDTO> getUserSpacesByName(
            @PathVariable(name = "username") String username,
            @PathVariable(name = "searchParam") String searchParam) {
        return service.getSpacesByName(username, searchParam).stream()
                .map(SpaceDTO::of)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Add space to specific user.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity addUserSpace(
            @RequestBody SpaceDTO spaceDTO) {
        service.addSpace(spaceDTO.toSpace());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{spaceId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(value = "Delete a specific user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteUserSpace(
            @PathVariable(name = "spaceId") long spaceId) {
        service.removeSpace(spaceId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT,
            produces = "application/json")
    @ApiOperation(value = "Edit space associated with specific user username.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity editSpaceName(
            @RequestBody SpaceDTO spaceDTO) {
        service.updateSpaceName(spaceDTO.toSpace());
        return ResponseEntity.ok().build();
    }

    /**
     * Change after file upload is done
     */
    @RequestMapping(
            value = "/{username}/{spaceId}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "Get all documents associated with specific user and space.", authorizations = {@Authorization(value = "apiKey")})
    public List<DocumentDTO> getAllDocuments(
            @PathVariable(name = "username") String username, @PathVariable(name = "spaceId") long spaceId) {
        return service.getAllDocumentsFromSpace(spaceId).stream()
                .map(DocumentDTO::DocumentDTOFromDocument)
                .collect(Collectors.toList());
    }

    @RequestMapping(
            value = "/{spaceId}/{documentId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation(value = "Delete specific document from user space.", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity deleteDocumentFromSpace(
            @PathVariable(name = "spaceId") long spaceId,
            @PathVariable(name = "documentId") long documentId) {
        service.removeDocumentFromSpace(spaceId, documentId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/{spaceId}/{documentId}",
            method = RequestMethod.PUT,
            produces = "application/json")
    @ApiOperation(
            value = "Edit a transcription of a particular document.",
            authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity editTranscription(
            @RequestBody DocumentDTO documentDTO) {
        service.editTranscription(documentDTO.DocumentFromDocumentDTO());
        return ResponseEntity.ok().build();
    }
}
