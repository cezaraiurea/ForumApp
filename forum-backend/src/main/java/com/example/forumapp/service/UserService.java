package com.example.forumapp.service;

import com.example.forumapp.entity.User;
import com.example.forumapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String insertUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already exists.";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        userRepository.save(user);
        return "User registered successfully.";
    }


    public String loginUser(User loginUser) {
        Optional<User> existingUser = userRepository.findByEmail(loginUser.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
                return "Login successful.";
            }
            else
            {
                return "Invalid password.";
            }
        }
        return "User not found!";
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        else
        {
            throw new RuntimeException("User not found with id: " + id);
        }
    }


    public String updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            user.setScore(updatedUser.getScore());
            userRepository.save(user);
            return "User updated successfully.";
        }
        else
        {
            return "User not found with id: " + id;
        }
    }


    public String deleteUserById(Long id) {
        if (userRepository.existsById(id))
        {
            userRepository.deleteById(id);
            return "User deleted successfully.";
        }
        else
        {
            return "User with id " + id + " does not exist.";
        }
    }


    public List<User> getBlockedUsers()
    { return userRepository.findByBlockedTrue(); }


    public String blockUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found.";
        }

        User user = optionalUser.get();
        user.setBlocked(true);
        emailService.sendBlockNotification(user.getEmail(), user.getUsername());
        userRepository.save(user);
        return "User blocked successfully.";
    }

    public String unblockUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return "User not found.";
        }

        User user = optionalUser.get();
        user.setBlocked(false);
        emailService.sendUnblockNotification(user.getEmail(), user.getUsername());
        userRepository.save(user);
        return "User unblocked successfully.";
    }


}
