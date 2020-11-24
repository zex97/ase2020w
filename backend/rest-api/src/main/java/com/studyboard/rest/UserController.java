package com.studyboard.rest;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.model.User;
import com.studyboard.space.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public User getUser(@PathVariable(name = "userId") long userId){
        return userService.getUser(userId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity createUser(@RequestBody User user) throws UniqueConstraintException {
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity editUserPassword(@RequestBody User user) {
        userService.updateUserPassword(user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity resetLoginAttempts(@PathVariable Long userId) {
        userService.resetLoginAttempts(userId);
        return ResponseEntity.ok().build();
    }

}
