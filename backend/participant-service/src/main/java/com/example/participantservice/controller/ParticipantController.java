package com.example.participantservice.controller;

import com.example.participantservice.model.Participant;
import com.example.participantservice.service.ParticipantService;
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

    @PostMapping
    public ResponseEntity<?> assign(@RequestBody Participant payload) {
        try {
            Participant p = participantService.assignToEvent(
                    payload.getUserId(),
                    payload.getEventId(),
                    payload.getFirstName(),
                    payload.getLastName(),
                    payload.getEmail(),
                    payload.getEventTitle());
            return ResponseEntity.status(201).body(p);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    public List<Participant> getAll() {
        return participantService.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<Participant> getByUser(@PathVariable String userId) {
        return participantService.getByUserId(userId);
    }

    @GetMapping("/event/{eventId}")
    public List<Participant> getByEvent(@PathVariable String eventId) {
        return participantService.getByEventId(eventId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean ok = participantService.delete(id);
        return ok
                ? ResponseEntity.ok(java.util.Collections.singletonMap("message", "deleted"))
                : ResponseEntity.notFound().build();
    }
}
