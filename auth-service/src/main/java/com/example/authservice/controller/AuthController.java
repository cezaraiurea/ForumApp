package com.example.authservice.controller;

import com.example.authservice.entity.User;
import com.example.authservice.security.JwtUtil;
import com.example.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        String result = userService.register(user);

        if (result.equals("User registered successfully.")) {
            User savedUser = userService.getByUsername(user.getUsername());
            String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setUserId(savedUser.getId());
            authResponse.setUsername(savedUser.getUsername());
            authResponse.setEmail(savedUser.getEmail());
            authResponse.setRole(savedUser.getRole());
            authResponse.setBlocked(savedUser.isBlocked());

            return ResponseEntity.ok(authResponse);
        }
        return ResponseEntity.status(409).body(null);
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) {
        User authenticatedUser = userService.authenticate(user);

        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        if (authenticatedUser.isBlocked()) {
            return ResponseEntity.status(403).body(new AuthResponse("Contul tau este blocat, nu poti accesa aplicatia.", null, null, null));
        }

        String token = jwtUtil.generateToken(authenticatedUser.getUsername(), authenticatedUser.getRole());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setUserId(authenticatedUser.getId());
        authResponse.setUsername(authenticatedUser.getUsername());
        authResponse.setEmail(authenticatedUser.getEmail());
        authResponse.setRole(authenticatedUser.getRole());
        authResponse.setBlocked(authenticatedUser.isBlocked());

        return ResponseEntity.ok(authResponse);
    }

}