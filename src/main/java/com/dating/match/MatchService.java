package com.dating.match;

import com.dating.profile.ProfileCard;
import com.dating.profile.ProfileService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final SwipeRepository swipes;
    private final MatchRepository matches;
    private final ProfileService profileService;

    @Transactional
    public SwipeResponse swipe(UUID swiperId, UUID targetId, boolean liked) {
        if (swiperId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot swipe on yourself");
        }
        if (!profileService.exists(targetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Match pair = Match.between(swiperId, targetId);
        swipes.lockPair(pair.pairKey());

        SwipeId swipeId = new SwipeId(swiperId, targetId);
        swipes.findById(swipeId).ifPresentOrElse(
                swipe -> swipe.setLiked(liked),
                () -> swipes.save(new Swipe(swipeId, liked)));

        Optional<Match> existing = matches.findByUserAAndUserB(pair.getUserA(), pair.getUserB());
        if (!liked) {
            existing.ifPresent(matches::delete);
            return new SwipeResponse(false, null);
        }
        if (!swipes.existsByIdAndLikedTrue(new SwipeId(targetId, swiperId))) {
            return new SwipeResponse(false, null);
        }
        Match match = existing.orElseGet(() -> matches.save(pair));
        return new SwipeResponse(true, match.getId());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> list(UUID userId, Pageable pageable) {
        List<Match> userMatches = matches.findAllForUser(userId, pageable);
        Map<UUID, ProfileCard> cards = profileService.cards(
                userMatches.stream().map(match -> match.otherThan(userId)).toList());
        return userMatches.stream()
                .map(match -> new MatchResponse(match.getId(), cards.get(match.otherThan(userId)), match.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void unmatch(UUID matchId, UUID userId) {
        Match match = requireParticipant(matchId, userId);
        swipe(userId, match.otherThan(userId), false);
    }

    @Transactional(readOnly = true)
    public Match requireParticipant(UUID matchId, UUID userId) {
        return matches.findById(matchId)
                .filter(match -> match.involves(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
    }
}
