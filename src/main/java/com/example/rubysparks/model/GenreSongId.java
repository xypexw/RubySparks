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
public class GenreSongId implements Serializable {

    @Column(name = "genre_id")
    private UUID genreId;

    @Column(name = "song_id")
    private UUID songId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreSongId that = (GenreSongId) o;
        return Objects.equals(genreId, that.genreId) && Objects.equals(songId, that.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, songId);
    }
}
