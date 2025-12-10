package com.example.eventapp.services;

import com.example.eventapp.models.Participant;
import com.example.eventapp.models.User;
import com.example.eventapp.repositories.ParticipantRepository;
import com.example.eventapp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ParticipantService(ParticipantRepository participantRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Vérifie si déjà inscrit
    public boolean isAlreadyRegistered(String userId, String eventId) {
        return participantRepository.existsByUserIdAndEventId(userId, eventId);
    }

    // Inscription
    public Participant assignToEvent(String userId, String eventId, String firstName, String lastName, String email, String eventTitle) {
        if (isAlreadyRegistered(userId, eventId)) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cet événement !");
        }

        User user = userRepository.findById(userId).orElse(null);

        Participant p = new Participant();
        p.setUserId(userId);
        p.setEventId(eventId);
        p.setEventTitle(eventTitle);
        p.setFirstName(firstName != null ? firstName : (user != null ? user.getFirstName() : ""));
        p.setLastName(lastName != null ? lastName : (user != null ? user.getLastName() : ""));
        p.setEmail(email != null ? email : (user != null ? user.getEmail() : ""));

        Participant saved = participantRepository.save(p);

        notificationService.createNotification(userId, "Vous êtes inscrit à l'événement : " + eventTitle);

        return saved;
    }

    // Liste toutes les inscriptions
    public List<Participant> getAll() {
        return participantRepository.findAll();
    }

    // Liste les inscriptions d’un utilisateur
    public List<Participant> getByUserId(String userId) {
        return participantRepository.findByUserId(userId);
    }

    // Supprimer inscription
    public boolean delete(String id) {
        Participant p = participantRepository.findById(id).orElse(null);
        if (p == null) return false;

        participantRepository.deleteById(id);

        notificationService.createNotification(p.getUserId(), "Vous vous êtes désinscrit de l'événement : " + p.getEventTitle());

        return true;
    }

    // Liste inscriptions par événement
    public List<Participant> getByEventId(String eventId) {
        return participantRepository.findByEventId(eventId);
    }
}
