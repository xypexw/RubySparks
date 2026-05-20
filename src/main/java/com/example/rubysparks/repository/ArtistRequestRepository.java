package com.example.rubysparks.repository;

import com.example.rubysparks.model.ArtistRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistRequestRepository extends JpaRepository<ArtistRequest, UUID> {
    List<ArtistRequest> findByUserUserId(UUID userId);
    List<ArtistRequest> findByStatus(String status);
}
