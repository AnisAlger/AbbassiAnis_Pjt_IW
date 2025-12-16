package com.example.authservice.controller;

import com.example.authservice.model.User;
import com.example.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User payload) {
        // payload might have "passwordHash" or similar, usually client sends "password"
        // reusing User model where passwordHash is used for storage.
        // Assuming client sends "passwordHash" field OR we just accept "password" in a
        // DTO.
        // For simplicity reusing User directly but treating passwordHash as input
        // password

        String pass = payload.getPasswordHash();

        User u = authService.register(
                payload.getEmail(),
                pass,
                payload.getRole(),
                payload.getFirstName(),
                payload.getLastName());

        return ResponseEntity.status(201).body(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.getOrDefault("password", payload.get("passwordHash"));

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email et mot de passe requis"));
        }

        try {
            Map<String, Object> resp = authService.login(email, password);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
