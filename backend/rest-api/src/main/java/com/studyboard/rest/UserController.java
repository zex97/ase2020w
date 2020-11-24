package com.studyboard.rest;

import com.studyboard.model.User;
import com.studyboard.space.security.service.UniqueConstraintException;
import com.studyboard.space.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = "application/json")
    public User getUser(@PathVariable(name = "userId") long userId){
        //TODO throw http exception
        return userService.getUser(userId);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public User createUser(@RequestBody User user) throws UniqueConstraintException {
        return userService.createUser(user);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public User editUser(@RequestBody User user) {
        return userService.updateUserPassword(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, produces = "application/json")
    public User invertEnabled(@PathVariable Long userId) {
        return userService.resetLoginAttempts(userId);
    }

}
