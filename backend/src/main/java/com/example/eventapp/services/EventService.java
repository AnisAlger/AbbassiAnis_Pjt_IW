package com.example.eventapp.services;

import com.example.eventapp.models.Event;
import com.example.eventapp.models.Participant;
import com.example.eventapp.repositories.EventRepository;
import com.example.eventapp.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final NotificationService notificationService;

    public EventService(EventRepository eventRepository,
                        ParticipantRepository participantRepository,
                        NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.notificationService = notificationService;
    }

    public Event create(Event e, String createdBy) {
        e.setCreatedBy(createdBy);
        e.setCreatedAt(Instant.now().toString());
        Event saved = eventRepository.save(e);

        // Notification créateur
        notificationService.send(createdBy,
            "Votre événement '" + e.getTitle() + "' a été créé avec succès.");

        return saved;
    }

    public Event update(ObjectId id, Event payload) {
    Event existing = eventRepository.findById(id).orElse(null);
    if (existing == null) return null;

    existing.setTitle(payload.getTitle());
    existing.setDescription(payload.getDescription());
    existing.setDate(payload.getDate());
    existing.setLocation(payload.getLocation());

    // Mettre à jour l'image si elle existe
    if (payload.getImageUrl() != null) {
        existing.setImageUrl(payload.getImageUrl());
    }

    Event saved = eventRepository.save(existing);

    // Notification créateur
    notificationService.send(existing.getCreatedBy(),
        "Votre événement '" + existing.getTitle() + "' a été modifié.");

    // Notification participants
    List<Participant> participants = participantRepository.findByEventId(id.toHexString());
    for (Participant p : participants) {
        notificationService.send(p.getUserId(),
            "L'événement '" + existing.getTitle() + "' auquel vous participez a été modifié.");
    }

    return saved;
}


    public boolean delete(ObjectId id) {
        Event e = eventRepository.findById(id).orElse(null);
        if (e == null) return false;

        // Notification participants avant suppression
        List<Participant> participants = participantRepository.findByEventId(id.toHexString());
        for (Participant p : participants) {
            notificationService.send(p.getUserId(),
                "L'événement '" + e.getTitle() + "' auquel vous participez a été supprimé.");
        }

        // Notification créateur
        notificationService.send(e.getCreatedBy(),
            "Votre événement '" + e.getTitle() + "' a été supprimé.");

        eventRepository.deleteById(id);

        return true;
    }

    public Iterable<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event getById(ObjectId id) {
        return eventRepository.findById(id).orElse(null);
    }
}
