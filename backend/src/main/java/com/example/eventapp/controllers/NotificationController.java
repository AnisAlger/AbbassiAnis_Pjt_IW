package com.example.eventapp.controllers;

import com.example.eventapp.models.Notification;
import com.example.eventapp.services.NotificationService;
import com.example.eventapp.utils.JwtUtil; // utilitaire pour extraire userId du JWT
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    // GET /notifications/me
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @AuthenticationPrincipal String userId
    ) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // PUT /notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal String userId
    ) {
        Notification n = notificationService.getNotificationById(id);

        if (n == null || !n.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        n.setRead(true);
        Notification updated = notificationService.saveNotification(n);
        return ResponseEntity.ok(updated);
    }

}
