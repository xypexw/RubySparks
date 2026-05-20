package com.example.rubysparks.repository;

import com.example.rubysparks.model.GenreSong;
import com.example.rubysparks.model.GenreSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GenreSongRepository extends JpaRepository<GenreSong, GenreSongId> {
    List<GenreSong> findByIdGenreId(UUID genreId);
    List<GenreSong> findByIdSongId(UUID songId);
}
