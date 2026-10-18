package com.dating.chat;

import com.dating.common.CurrentUserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches/{matchId}/messages")
@RequiredArgsConstructor
class ChatController {

    private final ChatService chatService;

    @GetMapping
    List<MessageResponse> history(
            @CurrentUserId UUID userId,
            @PathVariable UUID matchId,
            @RequestParam(required = false) Instant before,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        return chatService.history(matchId, userId, before, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MessageResponse send(
            @CurrentUserId UUID userId,
            @PathVariable UUID matchId,
            @Valid @RequestBody SendMessageRequest request) {
        return chatService.send(matchId, userId, request);
    }
}
