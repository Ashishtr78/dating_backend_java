package com.dating.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByMatchIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID matchId, Instant before, Limit limit);
}
