package com.example.rubysparks.repository;

import com.example.rubysparks.model.PlaylistSong;
import com.example.rubysparks.model.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    List<PlaylistSong> findByIdPlaylistId(UUID playlistId);
    List<PlaylistSong> findByIdSongId(UUID songId);
    void deleteByIdPlaylistId(UUID playlistId);
}
