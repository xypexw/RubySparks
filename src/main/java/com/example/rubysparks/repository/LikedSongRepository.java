package com.example.rubysparks.repository;

import com.example.rubysparks.model.LikedSong;
import com.example.rubysparks.model.LikedSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, LikedSongId> {
    List<LikedSong> findByIdUserId(UUID userId);
    List<LikedSong> findByIdSongId(UUID songId);
    boolean existsByIdUserIdAndIdSongId(UUID userId, UUID songId);
}
