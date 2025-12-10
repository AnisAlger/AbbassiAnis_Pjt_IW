package com.example.eventapp.services;

import com.example.eventapp.models.Notification;
import com.example.eventapp.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification createNotification(String userId, String message) {
        Notification n = new Notification(userId, message);
        return repo.save(n);
    }

    public List<Notification> getUserNotifications(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Pour faciliter l'envoi direct depuis d'autres services
    public void send(String userId, String message) {
        createNotification(userId, message);
    }

    public Notification getNotificationById(String id) {
    return repo.findById(id).orElse(null);
    }

    public Notification saveNotification(Notification n) {
    return repo.save(n);
    }

}
