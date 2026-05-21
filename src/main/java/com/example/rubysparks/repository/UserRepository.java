package com.example.rubysparks.repository;

import com.example.rubysparks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByStageNameIgnoreCase(String stageName);
    boolean existsByStageNameIgnoreCaseAndUserIdNot(String stageName, UUID userId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT u FROM User u WHERE " +
        "(:search IS NULL OR :search = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
        "(:role IS NULL OR :role = '' OR :role = 'ALL' OR u.role = :role) AND " +
        "(:status IS NULL OR :status = '' OR :status = 'ALL' OR u.status = :status)"
    )
    org.springframework.data.domain.Page<User> findAllFiltered(
        @org.springframework.data.repository.query.Param("search") String search,
        @org.springframework.data.repository.query.Param("role") String role,
        @org.springframework.data.repository.query.Param("status") String status,
        org.springframework.data.domain.Pageable pageable
    );
}
