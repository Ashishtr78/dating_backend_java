package com.dating.profile;

import java.util.List;
import java.util.UUID;

public record ProfileCard(UUID id, String displayName, int age, Gender gender, String bio, List<String> photoUrls) {

    public static ProfileCard from(User user) {
        return new ProfileCard(
                user.getId(),
                user.getDisplayName(),
                user.age(),
                user.getGender(),
                user.getBio(),
                List.copyOf(user.getPhotoUrls()));
    }
}
