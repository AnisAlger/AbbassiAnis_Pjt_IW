package com.example.eventapp.repositories;

import com.example.eventapp.models.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ParticipantRepository extends MongoRepository<Participant, String> {
    List<Participant> findByEventId(String eventId);
    List<Participant> findByUserId(String userId);  // ⭐️ IMPORTANT
    boolean existsByUserIdAndEventId(String userId, String eventId);
}
