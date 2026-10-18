package com.dating.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface SwipeRepository extends JpaRepository<Swipe, SwipeId> {

    boolean existsByIdAndLikedTrue(SwipeId id);

    @Query(nativeQuery = true, value = "select count(*) from pg_advisory_xact_lock(hashtextextended(:key, 0))")
    long lockPair(String key);
}
