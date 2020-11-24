package com.studyboard.space.security.service;

import com.studyboard.model.User;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SimpleUserService implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            //// TODO: throw new NotFoundException
        }
        return user;
    }

    @Override
    public User createUser(User user) throws UniqueConstraintException {
        //TODO hash password when backend connected to frontend
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintException("User with the same username or email already exists.");
        }
    }

    @Override
    public User updateUserPassword(User user) {
        //TODO encode the password before saving
        User storedUser = userRepository.findUserById(user.getId());
        if (storedUser == null) {
            //// TODO: throw new NotFoundException
        }
        storedUser.setPassword(user.getPassword());
        return userRepository.save(storedUser);
    }

    @Override
    public User resetLoginAttempts(Long id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            //// TODO: throw new NotFoundException
        }
        user.setLoginAttempts(0);
        return userRepository.save(user);
    }
}
