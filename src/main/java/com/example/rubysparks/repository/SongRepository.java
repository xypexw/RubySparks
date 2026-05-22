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

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Song s SET s.status = 'HIDDEN' WHERE s.ownerUserId = :ownerUserId")
    void hideAllByOwnerUserId(@org.springframework.data.repository.query.Param("ownerUserId") UUID ownerUserId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Song s SET s.artistName = :artistName WHERE s.ownerUserId = :ownerUserId")
    void updateArtistNameByOwnerUserId(@org.springframework.data.repository.query.Param("artistName") String artistName, @org.springframework.data.repository.query.Param("ownerUserId") UUID ownerUserId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Song s SET s.status = 'APPROVED' WHERE s.ownerUserId = :ownerUserId AND s.status = 'HIDDEN'")
    void restoreAllByOwnerUserId(@org.springframework.data.repository.query.Param("ownerUserId") UUID ownerUserId);
}
