package com.example.forumapp.service;

import com.example.forumapp.entity.User;
import com.example.forumapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUserSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@yahoo.com");
        user.setPassword("1234");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@yahoo.com")).thenReturn(false);

        String result = userService.insertUser(user);

        assertEquals("User registered successfully.", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void usernameExists() {
        User user = new User();
        user.setUsername("utilizator1");
        user.setEmail("user1@yahoo.com");
        user.setPassword("1234");

        when(userRepository.existsByUsername("utilizator1")).thenReturn(true);

        String result = userService.insertUser(user);

        assertEquals("Username already exists.", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void emailExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("cont123@gmail.com");
        user.setPassword("1234");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("cont123@gmail.com")).thenReturn(true);

        String result = userService.insertUser(user);

        assertEquals("Email already exists.", result);
        verify(userRepository, never()).save(any(User.class));
    }
}
