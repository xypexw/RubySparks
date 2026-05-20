package com.example.rubysparks.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreDTO {
    private UUID genreId;
    private String name;
    private LocalDateTime createdAt;
}
