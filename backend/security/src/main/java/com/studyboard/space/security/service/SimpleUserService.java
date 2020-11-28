package com.studyboard.space.security.service;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.User;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleUserService implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUser(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User createUser(User user) throws UniqueConstraintException {
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException("User with the same username or email already exists.");
        }
    }

    @Override
    public User updateUserPassword(User user) {
        User storedUser = userRepository.findUserById(user.getId());
        if (storedUser == null) {
            throw new UserDoesNotExist();
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        storedUser.setPassword(hashedPassword);
        storedUser.setPassword(user.getPassword());
        return userRepository.save(storedUser);
    }

    @Override
    public User resetLoginAttempts(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        user.setLoginAttempts(0);
        return userRepository.save(user);
    }
}
