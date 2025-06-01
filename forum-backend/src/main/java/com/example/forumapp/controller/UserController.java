package com.example.forumapp.controller;

import com.example.forumapp.entity.User;
import com.example.forumapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String result = userService.insertUser(user);
        if (result.equals("Username already exists.") || result.equals("Email already exists.")) {
            return ResponseEntity.status(409).body(result);
        }
        return ResponseEntity.status(201).body(result);
    }


    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
        String result = userService.loginUser(loginUser);
        if (result.equals("Login successful.")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(401).body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(404).body(null);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        String result = userService.updateUser(userId, updatedUser);
        if (result.equals("User not found")) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<User>> getBlockedUsers() {
        List<User> blockedUsers = userService.getBlockedUsers();
        return ResponseEntity.ok(blockedUsers);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        String result = userService.deleteUserById(userId);
        if (result.equals("User deleted successfully.")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(404).body(result);
    }

    @PutMapping("/block/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        String result = userService.blockUser(userId);
        if (result.equals("User not found.")) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }


    @PutMapping("/unblock/{userId}")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        String result = userService.unblockUser(userId);
        if (result.equals("User not found.")) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }





}
