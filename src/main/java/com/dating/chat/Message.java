package com.dating.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID matchId;

    private UUID senderId;

    private String body;

    @CreationTimestamp
    private Instant createdAt;

    public Message(UUID matchId, UUID senderId, String body) {
        this.matchId = matchId;
        this.senderId = senderId;
        this.body = body;
    }
}
