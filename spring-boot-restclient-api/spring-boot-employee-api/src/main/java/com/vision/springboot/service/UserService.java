package com.vision.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vision.springboot.entity.User;
import com.vision.springboot.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    // Declare the repository as final to ensure its immutability
    private final UserRepository userRepository;

    // Use constructor-based dependency injection
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
    