package com.dating.profile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        String displayName,
        LocalDate birthDate,
        Gender gender,
        InterestedIn interestedIn,
        String bio,
        List<String> photoUrls,
        Double latitude,
        Double longitude,
        int minAge,
        int maxAge,
        int maxDistanceKm) {

    static ProfileResponse from(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getBirthDate(),
                user.getGender(),
                user.getInterestedIn(),
                user.getBio(),
                List.copyOf(user.getPhotoUrls()),
                user.getLatitude(),
                user.getLongitude(),
                user.getMinAge(),
                user.getMaxAge(),
                user.getMaxDistanceKm());
    }
}
