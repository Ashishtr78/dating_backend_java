package com.dating.match;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "swipes")
@Getter
@Setter
@NoArgsConstructor
public class Swipe {

    @EmbeddedId
    private SwipeId id;

    private boolean liked;

    @CreationTimestamp
    private Instant createdAt;

    public Swipe(SwipeId id, boolean liked) {
        this.id = id;
        this.liked = liked;
    }
}
