package com.studyboard.rest;

import com.studyboard.dto.UserDTO;
import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.service.UserService;
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

    @RequestMapping(value = "/username{username}", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Get user with specific username.", authorizations = {@Authorization(value = "apiKey")})
    public UserDTO getUserByUsername(@PathVariable(name = "username") String username) {
        return UserDTO.of(userService.getUserByUsername(username));
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

    @RequestMapping(value = "/reset/{email}", method = RequestMethod.POST)
    @ApiOperation(value = "Check if email address exists and send recovery link if it does.")
    public ResponseEntity checkEmailAndRecover(@PathVariable(name = "email") String email) {
        userService.checkEmailAndRecover(email);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/reset/token/{token}", method = RequestMethod.POST)
    @ApiOperation(value = "Verify reset token.")
    public String verifyResetToken(@PathVariable(name = "token") String token) {
        if(userService.validateResetToken(token)) {
            return "{\"token\":\"" + token + "\"}";
        }
        return "{\"token\":\"invalid\"}";
    }

    @RequestMapping(value = "/reset/change/{token}", method = RequestMethod.POST)
    @ApiOperation(value = "Change user password with reset token.")
    public ResponseEntity changePasswordWithToken(@PathVariable(name = "token") String token, @RequestBody UserDTO userDTO) {
        if(userService.validateResetToken(token)) {
            userService.changePasswordWithToken(token, userDTO.getPassword());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
