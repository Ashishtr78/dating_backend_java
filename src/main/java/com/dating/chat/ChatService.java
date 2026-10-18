package com.dating.chat;

import com.dating.match.MatchService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ChatService {

    private final MessageRepository messages;
    private final MatchService matchService;

    @Transactional(readOnly = true)
    public List<MessageResponse> history(UUID matchId, UUID userId, Instant before, int size) {
        matchService.requireParticipant(matchId, userId);
        Instant cursor = before == null ? Instant.now() : before;
        return messages.findByMatchIdAndCreatedAtBeforeOrderByCreatedAtDesc(matchId, cursor, Limit.of(size)).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public MessageResponse send(UUID matchId, UUID userId, SendMessageRequest request) {
        matchService.requireParticipant(matchId, userId);
        return MessageResponse.from(messages.save(new Message(matchId, userId, request.body().strip())));
    }
}
