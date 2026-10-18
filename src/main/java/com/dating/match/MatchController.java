package com.dating.match;

import com.dating.common.CurrentUserId;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class MatchController {

    private final MatchService matchService;

    @PostMapping("/swipes")
    SwipeResponse swipe(@CurrentUserId UUID userId, @Valid @RequestBody SwipeRequest request) {
        return matchService.swipe(userId, request.targetId(), request.liked());
    }

    @GetMapping("/matches")
    List<MatchResponse> list(@CurrentUserId UUID userId, @PageableDefault(size = 50) Pageable pageable) {
        return matchService.list(userId, pageable);
    }

    @DeleteMapping("/matches/{matchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unmatch(@CurrentUserId UUID userId, @PathVariable UUID matchId) {
        matchService.unmatch(matchId, userId);
    }
}
