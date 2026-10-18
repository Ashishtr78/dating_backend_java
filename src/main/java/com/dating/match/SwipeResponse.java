package com.dating.match;

import java.util.UUID;

public record SwipeResponse(boolean matched, UUID matchId) {
}
