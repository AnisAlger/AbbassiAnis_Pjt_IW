package com.example.eventservice.repository;

import com.example.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

public interface EventRepository extends MongoRepository<Event, ObjectId> {
}
