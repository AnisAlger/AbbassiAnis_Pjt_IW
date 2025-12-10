package com.example.eventapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

@Document(collection = "events")
public class Event {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private String date;
    private String location;
    private String createdBy;
    private String createdAt;
    private String imageUrl; // nouveau champ

    public Event() {}

    public String getId() { return id != null ? id.toHexString() : null; }
    public void setId(String id) { this.id = id != null ? new ObjectId(id) : null; }

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public String getDate(){ return date; }
    public void setDate(String date){ this.date = date; }

    public String getLocation(){ return location; }
    public void setLocation(String location){ this.location = location; }

    public String getCreatedBy(){ return createdBy; }
    public void setCreatedBy(String createdBy){ this.createdBy = createdBy; }

    public String getCreatedAt(){ return createdAt; }
    public void setCreatedAt(String createdAt){ this.createdAt = createdAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
