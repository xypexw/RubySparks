package com.example.rubysparks.repository;

import com.example.rubysparks.model.PlaylistFollow;
import com.example.rubysparks.model.PlaylistFollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistFollowRepository extends JpaRepository<PlaylistFollow, PlaylistFollowId> {
    List<PlaylistFollow> findByIdUserId(UUID userId);
    List<PlaylistFollow> findByIdPlaylistId(UUID playlistId);
}
