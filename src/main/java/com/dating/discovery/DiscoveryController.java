package com.dating.discovery;

import com.dating.common.CurrentUserId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping
    List<Candidate> candidates(
            @CurrentUserId UUID userId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit) {
        return discoveryService.candidates(userId, limit);
    }
}
