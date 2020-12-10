package com.studyboard.space.user.service;

import com.studyboard.exception.UniqueConstraintException;
import com.studyboard.exception.UserDoesNotExist;
import com.studyboard.model.Authorities;
import com.studyboard.model.User;
import com.studyboard.repository.AuthoritiesRepository;
import com.studyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

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
    public User getUserByUsername(String username) {
        User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UserDoesNotExist();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) throws UniqueConstraintException {
        try {
            authoritiesRepository.save(new Authorities(user.getUsername(), "USER"));
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setRole("USER");
            user.setEnabled(true);
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
