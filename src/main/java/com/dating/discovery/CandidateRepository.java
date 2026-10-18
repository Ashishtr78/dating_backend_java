package com.dating.discovery;

import com.dating.profile.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

interface CandidateRepository extends Repository<User, UUID> {

    // ponytail: full scan with per-row haversine; add PostGIS or a lat/lng bounding-box index past ~100k users
    @Query(nativeQuery = true, value = """
            select u.* from users u
            join users me on me.id = :userId
            where u.id <> me.id
              and u.latitude is not null
              and (me.interested_in = 'EVERYONE' or me.interested_in = u.gender)
              and (u.interested_in = 'EVERYONE' or u.interested_in = me.gender)
              and date_part('year', age(u.birth_date)) between me.min_age and me.max_age
              and date_part('year', age(me.birth_date)) between u.min_age and u.max_age
              and not exists (select 1 from swipes s where s.swiper_id = me.id and s.target_id = u.id)
              and not exists (select 1 from swipes s where s.swiper_id = u.id and s.target_id = me.id and not s.liked)
              and 6371 * acos(least(1.0,
                    cos(radians(me.latitude)) * cos(radians(u.latitude)) * cos(radians(u.longitude - me.longitude))
                    + sin(radians(me.latitude)) * sin(radians(u.latitude)))) <= me.max_distance_km
            order by u.updated_at desc
            limit :limit
            """)
    List<User> findCandidates(UUID userId, int limit);
}
