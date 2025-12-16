package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET /notifications/me : Mes notifications
    @GetMapping("/me")
    public List<Notification> getMyNotifications(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return notificationService.getUserNotifications(userId);
    }

    // PUT /notifications/{id}/read : Marquer comme lu
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        Notification n = notificationService.markAsRead(id);
        if (n == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(n);
    }

    // POST /notifications : Interne (ou admin) pour créer une notif
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String message = payload.get("message");
        if (userId == null || message == null)
            return ResponseEntity.badRequest().build();

        Notification n = notificationService.create(userId, message);
        return ResponseEntity.status(201).body(n);
    }
}
