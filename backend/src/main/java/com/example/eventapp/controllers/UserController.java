package com.example.eventapp.controllers;

import com.example.eventapp.models.User;
import com.example.eventapp.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint pour récupérer uniquement les participants
    @GetMapping("/participants")
    public List<User> getParticipants() {
        return userRepository.findByRole("participant");
    }
}
