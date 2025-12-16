package com.example.participantservice.repository;

import com.example.participantservice.model.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ParticipantRepository extends MongoRepository<Participant, String> {
    List<Participant> findByEventId(String eventId);

    List<Participant> findByUserId(String userId);

    boolean existsByUserIdAndEventId(String userId, String eventId);
}
