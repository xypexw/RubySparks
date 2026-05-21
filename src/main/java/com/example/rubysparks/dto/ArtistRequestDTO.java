package com.example.rubysparks.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistRequestDTO {
    private UUID requestId;
    private UUID userId;
    private String userName;
    private String email;
    private String avatarUrl;
    private String stageName;
    private String genre;
    private String bio;
    private String status;
    private LocalDateTime createdAt;
}
