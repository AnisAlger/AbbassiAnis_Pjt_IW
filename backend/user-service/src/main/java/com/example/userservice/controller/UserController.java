package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/participants")
    public List<User> getParticipants() {
        return userRepository.findByRole("participant");
    }

    // Additional endpoints to manage users (e.g., getting current user profile)
    @GetMapping("/me")
    public User getMe(@RequestAttribute(name = "id", required = false) String userId) {
        // Note: userId might come from token claims in security context
        // Implementing simple find all for now or specific retrieval
        return null; // TODO implement specific logical reading from Context
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }
}
