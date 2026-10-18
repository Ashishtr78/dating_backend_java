package com.dating.match;

import com.dating.profile.ProfileCard;
import java.time.Instant;
import java.util.UUID;

public record MatchResponse(UUID id, ProfileCard profile, Instant matchedAt) {
}
