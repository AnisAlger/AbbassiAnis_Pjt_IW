package com.example.eventservice.service;

import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate;

    public EventService(EventRepository eventRepository, RestTemplate restTemplate) {
        this.eventRepository = eventRepository;
        this.restTemplate = restTemplate;
    }

    public Event create(Event e, String createdBy) {
        e.setCreatedBy(createdBy);
        e.setCreatedAt(Instant.now().toString());
        Event saved = eventRepository.save(e);

        // Notification créateur via Notification Service
        sendNotification(createdBy, "Votre événement '" + e.getTitle() + "' a été créé avec succès.");

        return saved;
    }

    public Event update(ObjectId id, Event payload) {
        Event existing = eventRepository.findById(id).orElse(null);
        if (existing == null)
            return null;

        existing.setTitle(payload.getTitle());
        existing.setDescription(payload.getDescription());
        existing.setDate(payload.getDate());
        existing.setLocation(payload.getLocation());

        if (payload.getImageUrl() != null) {
            existing.setImageUrl(payload.getImageUrl());
        }

        Event saved = eventRepository.save(existing);

        // Notification créateur
        sendNotification(existing.getCreatedBy(), "Votre événement '" + existing.getTitle() + "' a été modifié.");

        // Notification participants via Participant Service
        notifyParticipants(id.toHexString(),
                "L'événement '" + existing.getTitle() + "' auquel vous participez a été modifié.");

        return saved;
    }

    public boolean delete(ObjectId id) {
        Event e = eventRepository.findById(id).orElse(null);
        if (e == null)
            return false;

        // Notification participants
        notifyParticipants(id.toHexString(),
                "L'événement '" + e.getTitle() + "' auquel vous participez a été supprimé.");

        // Notification créateur
        sendNotification(e.getCreatedBy(), "Votre événement '" + e.getTitle() + "' a été supprimé.");

        eventRepository.deleteById(id);
        return true;
    }

    public Iterable<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event getById(ObjectId id) {
        return eventRepository.findById(id).orElse(null);
    }

    // Helper methods for calling other microservices

    private void sendNotification(String userId, String message) {
        try {
            // Using service name 'notification-service'
            String url = "http://notification-service/notifications";
            // Payload
            Map<String, String> payload = Map.of("userId", userId, "message", message);
            restTemplate.postForObject(url, payload, Object.class);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    private void notifyParticipants(String eventId, String message) {
        try {
            // Get participants from participant-service
            String url = "http://participant-service/participants/event/" + eventId;
            // Assuming we get a list of objects with userId
            List<Map<String, Object>> participants = restTemplate.getForObject(url, List.class);
            if (participants != null) {
                for (Map<String, Object> p : participants) {
                    String uId = (String) p.get("userId");
                    sendNotification(uId, message);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to notify participants: " + e.getMessage());
        }
    }
}
