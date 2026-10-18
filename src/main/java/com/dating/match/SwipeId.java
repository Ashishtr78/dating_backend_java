package com.dating.match;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record SwipeId(UUID swiperId, UUID targetId) implements Serializable {
}
