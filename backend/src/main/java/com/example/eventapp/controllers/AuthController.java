package com.example.eventapp.controllers;

import com.example.eventapp.models.User;
import com.example.eventapp.services.AuthService;
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

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User payload) {

        User u = authService.register(
                payload.getEmail(),
                payload.getPasswordHash(), // côté client, envoyer "password" et adapter si nécessaire
                payload.getRole(),
                payload.getFirstName(),
                payload.getLastName()
        );

        return ResponseEntity.status(201).body(u);
    }

    // LOGIN - now accepts a simple payload map and returns token + user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        // support both keys: "password" (client typical) or "passwordHash" if client sends that
        String password = payload.getOrDefault("password", payload.get("passwordHash"));

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email et mot de passe requis"));
        }

        Map<String, Object> resp = authService.login(email, password);
        return ResponseEntity.ok(resp);
    }
}
