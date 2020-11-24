package com.studyboard.space.security.service;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExistException;
import com.studyboard.model.User;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleUserService implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserDoesNotExistException();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) throws UniqueConstraintException {
        //TODO hash password after authentication is done
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException("User with the same username or email already exists.");
        }
    }

    @Override
    public User updateUserPassword(User user) {
        //TODO hash password after authentication is done
        User storedUser = userRepository.findUserById(user.getId());
        if (storedUser == null) {
            throw new UserDoesNotExistException();
        }
        storedUser.setPassword(user.getPassword());
        return userRepository.save(storedUser);
    }

    @Override
    public User resetLoginAttempts(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserDoesNotExistException();
        }
        user.setLoginAttempts(0);
        return userRepository.save(user);
    }
}
