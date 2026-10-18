package com.dating.chat;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID id, UUID senderId, String body, Instant sentAt) {

    static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getSenderId(), message.getBody(), message.getCreatedAt());
    }
}
