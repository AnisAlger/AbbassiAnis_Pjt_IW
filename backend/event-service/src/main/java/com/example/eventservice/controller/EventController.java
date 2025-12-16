package com.example.eventservice.controller;

import com.example.eventservice.model.Event;
import com.example.eventservice.service.EventService;
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

    @GetMapping
    public Iterable<Event> getAll() {
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Event e = eventService.getById(new ObjectId(id));
        if (e == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createWithImage(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication auth) {
        String userId = (String) auth.getPrincipal();

        Event e = new Event();
        e.setTitle(title);
        e.setDescription(description);
        e.setDate(date);
        e.setLocation(location);

        if (image != null && !image.isEmpty()) {
            try {
                // Sanitize filename: remove all non-alphanumeric chars except dots and hyphens
                String originalFilename = image.getOriginalFilename();
                String sanitizedFilename = originalFilename != null
                        ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                        : "image.png";
                String filename = System.currentTimeMillis() + "_" + sanitizedFilename;

                // Saving locally in event-service container/folder
                // In microservice, ideally use S3/External storage or shared volume
                // For this "zip" delivery, local storage is fine
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

    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<?> create(@RequestBody Event payload, Authentication auth) {
        String userId = (String) auth.getPrincipal();
        Event created = eventService.create(payload, userId);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateWithImage(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Event e = new Event(); // DTO wrapper basically
        e.setTitle(title);
        e.setDescription(description);
        e.setDate(date);
        e.setLocation(location);

        if (image != null && !image.isEmpty()) {
            try {
                // Sanitize filename: remove all non-alphanumeric chars except dots and hyphens
                String originalFilename = image.getOriginalFilename();
                String sanitizedFilename = originalFilename != null
                        ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
                        : "image.png";
                String filename = System.currentTimeMillis() + "_" + sanitizedFilename;

                Path filePath = Paths.get("uploads").resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());
                e.setImageUrl("/uploads/" + filename);
            } catch (IOException ex) {
                return ResponseEntity.status(500).body("Erreur upload image");
            }
        }

        Event updated = eventService.update(new ObjectId(id), e);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Event payload) {
        Event updated = eventService.update(new ObjectId(id), payload);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean ok = eventService.delete(new ObjectId(id));
        if (!ok)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "deleted"));
    }
}
