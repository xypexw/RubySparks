package com.example.rubysparks.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDTO {
    private UUID songId;
    private String title;
    private String artistName;
    private String source;
    private String itunesId;
    private String previewUrl;
    private String fileUrl;
    private String thumbnailUrl;
    private String duration;
    private String status;
    private UUID ownerUserId;
    private String rejectReason;
    private String description;
    private List<String> genreIds;
    private LocalDateTime createdAt;
    private Long listenCount;
}

