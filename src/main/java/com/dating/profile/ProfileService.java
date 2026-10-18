package com.dating.profile;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository users;

    @Transactional(readOnly = true)
    public ProfileResponse get(UUID userId) {
        return ProfileResponse.from(require(userId));
    }

    @Transactional
    public ProfileResponse update(UUID userId, UpdateProfileRequest request) {
        if (request.minAge() > request.maxAge()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minAge must not exceed maxAge");
        }
        User user = require(userId);
        user.setDisplayName(request.displayName().strip());
        user.setBio(request.bio() == null || request.bio().isBlank() ? null : request.bio().strip());
        user.setGender(request.gender());
        user.setInterestedIn(request.interestedIn());
        user.getPhotoUrls().clear();
        user.getPhotoUrls().addAll(request.photoUrls());
        user.setMinAge(request.minAge());
        user.setMaxAge(request.maxAge());
        user.setMaxDistanceKm(request.maxDistanceKm());
        return ProfileResponse.from(user);
    }

    @Transactional
    public void updateLocation(UUID userId, UpdateLocationRequest request) {
        User user = require(userId);
        user.setLatitude(request.latitude());
        user.setLongitude(request.longitude());
    }

    @Transactional
    public void delete(UUID userId) {
        users.delete(require(userId));
    }

    @Transactional(readOnly = true)
    public boolean exists(UUID userId) {
        return users.existsById(userId);
    }

    @Transactional(readOnly = true)
    public Map<UUID, ProfileCard> cards(Collection<UUID> userIds) {
        return users.findAllById(userIds).stream()
                .map(ProfileCard::from)
                .collect(Collectors.toMap(ProfileCard::id, Function.identity()));
    }

    private User require(UUID userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
