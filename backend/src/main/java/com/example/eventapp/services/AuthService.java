package com.example.eventapp.services;

import com.example.eventapp.models.User;
import com.example.eventapp.repositories.UserRepository;
import com.example.eventapp.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Nouvelle version de register() avec firstName et lastName
     */
    public User register(String email, String password, String role, String firstName, String lastName) {

        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email already exists");
        }

        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role == null ? "organizer" : role);
        u.setFirstName(firstName);
        u.setLastName(lastName);

        return userRepository.save(u);
    }


    /**
     * Authentification - return token + user (sans mot de passe)
     */
    public Map<String, Object> login(String email, String password) {
        User u = userRepository.findByEmail(email);
        if (u == null) throw new RuntimeException("Invalid credentials");

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // token JWT qui contient userId + role
        String token = jwtUtil.generateToken(u.getId(), u.getRole());

        // prepare user payload (sans password)
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", u.getId());
        userMap.put("email", u.getEmail());
        userMap.put("firstName", u.getFirstName());
        userMap.put("lastName", u.getLastName());
        userMap.put("role", u.getRole());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("user", userMap);

        return resp;
    }
}
