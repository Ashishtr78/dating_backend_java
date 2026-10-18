package com.dating.match;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Match> findByUserAAndUserB(UUID userA, UUID userB);

    @Query("select m from Match m where m.userA = :userId or m.userB = :userId order by m.createdAt desc")
    List<Match> findAllForUser(UUID userId, Pageable pageable);
}
