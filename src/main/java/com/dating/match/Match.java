package com.dating.match;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_a")
    private UUID userA;

    @Column(name = "user_b")
    private UUID userB;

    @CreationTimestamp
    private Instant createdAt;

    private Match(UUID userA, UUID userB) {
        this.userA = userA;
        this.userB = userB;
    }

    static Match between(UUID first, UUID second) {
        return first.toString().compareTo(second.toString()) < 0 ? new Match(first, second) : new Match(second, first);
    }

    public boolean involves(UUID userId) {
        return userA.equals(userId) || userB.equals(userId);
    }

    public UUID otherThan(UUID userId) {
        return userA.equals(userId) ? userB : userA;
    }

    String pairKey() {
        return userA + ":" + userB;
    }
}
