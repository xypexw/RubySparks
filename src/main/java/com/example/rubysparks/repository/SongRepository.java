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
    java.util.Optional<Song> findByItunesId(String itunesId);
    java.util.Optional<Song> findByFileUrl(String fileUrl);
    java.util.Optional<Song> findByPreviewUrl(String previewUrl);
    java.util.Optional<Song> findByTitleAndArtistNameAndSource(String title, String artistName, String source);
}
