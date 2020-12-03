package com.studyboard.rest;

import com.studyboard.dto.UserDTO;
import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.space.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/user")
@Api(value = "api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Get user with specific id.", authorizations = {@Authorization(value = "apiKey")})
    public UserDTO getUser(@PathVariable(name = "userId") long userId) {
        return UserDTO.of(userService.getUser(userId));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Get all users.", authorizations = {@Authorization(value = "apiKey")})
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(UserDTO::of).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(value = "Create user.")
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) throws UniqueConstraintException {
        userService.createUser(userDTO.toUser());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    @ApiOperation(value = "Edit user password", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity editUserPassword(@RequestBody UserDTO userDTO) {
        userService.updateUserPassword(userDTO.toUser());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, produces = "application/json")
    @ApiOperation(value = "Reset login attempts for user with specific id", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity resetLoginAttempts(@PathVariable Long userId) {
        userService.resetLoginAttempts(userId);
        return ResponseEntity.ok().build();
    }

}
