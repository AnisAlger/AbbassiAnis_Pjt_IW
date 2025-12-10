package com.example.eventapp.controllers;

import com.example.eventapp.models.Participant;
import com.example.eventapp.services.ParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    // Inscription
    @PostMapping
    public ResponseEntity<?> assign(@RequestBody Participant payload) {
        try {
            Participant p = participantService.assignToEvent(
                    payload.getUserId(),
                    payload.getEventId(),
                    payload.getFirstName(),
                    payload.getLastName(),
                    payload.getEmail(),
                    payload.getEventTitle()
            );
            return ResponseEntity.status(201).body(p);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Récupérer toutes les inscriptions (admin)
    @GetMapping
    public List<Participant> getAll() {
        return participantService.getAll();
    }

    // Récupérer ses inscriptions (user)
    @GetMapping("/user/{userId}")
    public List<Participant> getByUser(@PathVariable String userId) {
        return participantService.getByUserId(userId);
    }

    // Désinscription
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean ok = participantService.delete(id);
        return ok
                ? ResponseEntity.ok(java.util.Collections.singletonMap("message", "deleted"))
                : ResponseEntity.notFound().build();
    }
}
