package com.dating.match;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SwipeRequest(@NotNull UUID targetId, @NotNull Boolean liked) {
}
