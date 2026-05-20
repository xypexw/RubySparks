package com.example.rubysparks.repository;

import com.example.rubysparks.model.ListenHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ListenHistoryRepository extends JpaRepository<ListenHistory, UUID> {
    List<ListenHistory> findByUserUserIdOrderByListenedAtDesc(UUID userId);
    List<ListenHistory> findBySongSongId(UUID songId);
}
