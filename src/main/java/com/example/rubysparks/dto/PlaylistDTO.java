package com.example.rubysparks.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistDTO {
    private UUID playlistId;
    private UUID userId;
    private String name;
    private String status;
    private LocalDateTime createdAt;
}
