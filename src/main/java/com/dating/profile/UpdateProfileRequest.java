package com.dating.profile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 50) String displayName,
        @Size(max = 500) String bio,
        @NotNull Gender gender,
        @NotNull InterestedIn interestedIn,
        @NotNull @Size(max = 6) List<@NotNull @Size(max = 500) @Pattern(regexp = "https?://\\S+", message = "must be an http(s) URL") String> photoUrls,
        @Min(18) @Max(99) int minAge,
        @Min(18) @Max(99) int maxAge,
        @Min(1) @Max(500) int maxDistanceKm) {
}
