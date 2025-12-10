package com.example.eventapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "participants")
public class Participant {

    @Id
    private String id;
    private String firstName; // déjà existant
    private String lastName;  // déjà existant
    private String email;
    private String eventId;
    private String userId;
    private String eventTitle;
    // ⚠️ Ajouter userId temporaire pour le POST


    public Participant() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventTitle() {
    return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
    this.eventTitle = eventTitle;
    }
}
