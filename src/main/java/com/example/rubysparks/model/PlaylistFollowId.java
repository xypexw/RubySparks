package com.example.rubysparks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistFollowId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "playlist_id")
    private UUID playlistId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistFollowId that = (PlaylistFollowId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(playlistId, that.playlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, playlistId);
    }
}
