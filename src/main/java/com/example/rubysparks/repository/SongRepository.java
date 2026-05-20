package com.example.rubysparks.repository;

import com.example.rubysparks.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    List<Song> findByTitleContainingIgnoreCase(String title);
    List<Song> findByArtistNameContainingIgnoreCase(String artistName);
}
