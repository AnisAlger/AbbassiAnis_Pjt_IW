package com.example.eventapp.controllers;

import com.example.eventapp.models.Event;
import com.example.eventapp.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 🔹 Récupérer tous les événements
    @GetMapping
    public Iterable<Event> getAll() {
        return eventService.getAll();
    }

    // 🔹 Récupérer un événement par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Event e = eventService.getById(new ObjectId(id));
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    // 🔹 Création événement avec image (multipart/form-data)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createWithImage(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication auth
    ) {
        String userId = (String) auth.getPrincipal();

        Event e = new Event();
        e.setTitle(title);
        e.setDescription(description);
        e.setDate(date);
        e.setLocation(location);

        // Gestion image
        if (image != null && !image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get("uploads").resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());

                e.setImageUrl("/uploads/" + filename);
            } catch (IOException ex) {
                return ResponseEntity.status(500).body("Erreur lors de l'upload de l'image");
            }
        }

        Event created = eventService.create(e, userId);
        return ResponseEntity.status(201).body(created);
    }

    // 🔹 Création événement sans image (JSON)
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<?> create(@RequestBody Event payload, Authentication auth) {
        String userId = (String) auth.getPrincipal();
        Event created = eventService.create(payload, userId);
        return ResponseEntity.status(201).body(created);
    }

    // 🔹 Mise à jour événement avec image
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateWithImage(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Event existing = eventService.getById(new ObjectId(id));
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setTitle(title);
        existing.setDescription(description);
        existing.setDate(date);
        existing.setLocation(location);

        if (image != null && !image.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get("uploads").resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());

                existing.setImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Erreur upload image");
            }
        }

        Event updated = eventService.update(new ObjectId(id), existing);
        return ResponseEntity.ok(updated);
    }

    // 🔹 Mise à jour événement sans image (JSON)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Event payload) {
        Event updated = eventService.update(new ObjectId(id), payload);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // 🔹 Suppression événement
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean ok = eventService.delete(new ObjectId(id));
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "deleted"));
    }
}
