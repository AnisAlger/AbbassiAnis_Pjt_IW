package com.example.eventapp.repositories;

import com.example.eventapp.models.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

public interface EventRepository extends MongoRepository<Event, ObjectId> { }
