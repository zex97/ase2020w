package com.studyboard.rest;

import com.studyboard.dto.UserDTO;
import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.space.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public UserDTO getUser(@PathVariable(name = "userId") long userId) {
        return UserDTO.of(userService.getUser(userId));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(UserDTO::of).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createUser(@RequestBody UserDTO userDTO) throws UniqueConstraintException {
        userService.createUser(userDTO.toUser());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity editUserPassword(@RequestBody UserDTO userDTO) {
        userService.updateUserPassword(userDTO.toUser());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity resetLoginAttempts(@PathVariable Long userId) {
        userService.resetLoginAttempts(userId);
        return ResponseEntity.ok().build();
    }

}
