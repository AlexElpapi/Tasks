package com.greenwave.todolist.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.greenwave.todolist.model.User;
import com.greenwave.todolist.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        // Codifica password prima di salvare
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("USER"); // ruolo di default
        userRepository.save(user);
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
    public List<User> findAll() {
    return userRepository.findAll();
}
}
