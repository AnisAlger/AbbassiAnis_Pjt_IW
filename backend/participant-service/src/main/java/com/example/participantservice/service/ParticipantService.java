package com.example.participantservice.service;

import com.example.participantservice.model.Participant;
import com.example.participantservice.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final RestTemplate restTemplate;

    public ParticipantService(ParticipantRepository participantRepository, RestTemplate restTemplate) {
        this.participantRepository = participantRepository;
        this.restTemplate = restTemplate;
    }

    public boolean isAlreadyRegistered(String userId, String eventId) {
        return participantRepository.existsByUserIdAndEventId(userId, eventId);
    }

    public Participant assignToEvent(String userId, String eventId, String firstName, String lastName, String email,
            String eventTitle) {
        if (isAlreadyRegistered(userId, eventId)) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cet événement !");
        }

        // Optional: Call user-service to fill details if missing
        // For now, assuming provided or just stored as is (decoupled)

        Participant p = new Participant();
        p.setUserId(userId);
        p.setEventId(eventId);
        p.setEventTitle(eventTitle);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setEmail(email);

        Participant saved = participantRepository.save(p);

        // Notification logic via notification-service
        sendNotification(userId, "Vous êtes inscrit à l'événement : " + eventTitle);

        return saved;
    }

    public List<Participant> getAll() {
        return participantRepository.findAll();
    }

    public List<Participant> getByUserId(String userId) {
        return participantRepository.findByUserId(userId);
    }

    public List<Participant> getByEventId(String eventId) {
        return participantRepository.findByEventId(eventId);
    }

    public boolean delete(String id) {
        Participant p = participantRepository.findById(id).orElse(null);
        if (p == null)
            return false;

        participantRepository.deleteById(id);

        sendNotification(p.getUserId(), "Vous vous êtes désinscrit de l'événement : " + p.getEventTitle());

        return true;
    }

    private void sendNotification(String userId, String message) {
        try {
            String url = "http://notification-service/notifications";
            Map<String, String> payload = Map.of("userId", userId, "message", message);
            restTemplate.postForObject(url, payload, Object.class);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}
