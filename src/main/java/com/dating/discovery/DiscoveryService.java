package com.dating.discovery;

import com.dating.profile.ProfileCard;
import com.dating.profile.User;
import com.dating.profile.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
class DiscoveryService {

    private static final double EARTH_RADIUS_KM = 6371;

    private final UserRepository users;
    private final CandidateRepository candidates;

    @Transactional(readOnly = true)
    public List<Candidate> candidates(UUID userId, int limit) {
        User me = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!me.hasLocation()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Set your location before discovering profiles");
        }
        return candidates.findCandidates(userId, limit).stream()
                .map(user -> new Candidate(ProfileCard.from(user), Math.round(distanceKm(me, user))))
                .toList();
    }

    static double distanceKm(User a, User b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double deltaLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double cosine = Math.cos(lat1) * Math.cos(lat2) * Math.cos(deltaLon) + Math.sin(lat1) * Math.sin(lat2);
        return EARTH_RADIUS_KM * Math.acos(Math.min(1.0, cosine));
    }
}
