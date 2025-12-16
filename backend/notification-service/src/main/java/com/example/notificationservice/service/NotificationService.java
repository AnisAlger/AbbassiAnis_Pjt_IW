package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(String id) {
        Notification n = notificationRepository.findById(id).orElse(null);
        if (n != null) {
            n.setRead(true);
            return notificationRepository.save(n);
        }
        return null;
    }

    public Notification create(String userId, String message) {
        return notificationRepository.save(new Notification(userId, message));
    }
}
