package com.dating.match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dating.profile.ProfileService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class MatchServiceTest {

    private final UUID alice = UUID.randomUUID();
    private final UUID bob = UUID.randomUUID();

    private SwipeRepository swipes;
    private MatchRepository matches;
    private MatchService service;

    @BeforeEach
    void setUp() {
        swipes = mock(SwipeRepository.class);
        matches = mock(MatchRepository.class);
        ProfileService profiles = mock(ProfileService.class);
        when(profiles.exists(any())).thenReturn(true);
        when(swipes.findById(any())).thenReturn(Optional.empty());
        when(matches.findByUserAAndUserB(any(), any())).thenReturn(Optional.empty());
        when(matches.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new MatchService(swipes, matches, profiles);
    }

    @Test
    void likeWithoutReciprocationDoesNotMatch() {
        SwipeResponse response = service.swipe(alice, bob, true);

        assertThat(response.matched()).isFalse();
        verify(matches, never()).save(any());
    }

    @Test
    void mutualLikeCreatesMatch() {
        when(swipes.existsByIdAndLikedTrue(new SwipeId(bob, alice))).thenReturn(true);

        SwipeResponse response = service.swipe(alice, bob, true);

        assertThat(response.matched()).isTrue();
        verify(matches).save(any(Match.class));
    }

    @Test
    void passRemovesExistingMatch() {
        Match existing = Match.between(alice, bob);
        when(matches.findByUserAAndUserB(existing.getUserA(), existing.getUserB())).thenReturn(Optional.of(existing));

        SwipeResponse response = service.swipe(alice, bob, false);

        assertThat(response.matched()).isFalse();
        verify(matches).delete(existing);
    }

    @Test
    void cannotSwipeOnSelf() {
        assertThatThrownBy(() -> service.swipe(alice, alice, true)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void pairOrderingIsCanonical() {
        assertThat(Match.between(alice, bob).pairKey()).isEqualTo(Match.between(bob, alice).pairKey());
    }
}
