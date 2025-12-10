package com.example.eventapp.schedulers;

import com.example.eventapp.models.Event;
import com.example.eventapp.models.Participant;
import com.example.eventapp.repositories.EventRepository;
import com.example.eventapp.repositories.ParticipantRepository;
import com.example.eventapp.services.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class EventReminderScheduler {

    private final EventRepository eventRepo;
    private final ParticipantRepository participantRepo;
    private final NotificationService notifService;

    public EventReminderScheduler(EventRepository eventRepo,
                                  ParticipantRepository participantRepo,
                                  NotificationService notifService) {
        this.eventRepo = eventRepo;
        this.participantRepo = participantRepo;
        this.notifService = notifService;
    }

    @Scheduled(fixedRate = 60_000) // toutes les 60 secondes
    public void sendReminders() {
        List<Event> events = eventRepo.findAll();
        Instant now = Instant.now();
        for (Event e : events) {
            Instant eventTime = Instant.parse(e.getDate());
            if (eventTime.minusSeconds(86400).isBefore(now) && eventTime.isAfter(now)) {
                // rappel 1h avant
                List<Participant> participants = participantRepo.findByEventId(e.getId());
                for (Participant p : participants) {
                    notifService.createNotification(p.getUserId(),
                        "Rappel : votre événement '" + e.getTitle() + "' commence bientôt.");
                }
            }
        }
    }
}
