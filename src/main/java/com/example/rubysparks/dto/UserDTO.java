package com.example.rubysparks.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID userId;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String stageName;
    private String status;
    private String banReason;
    private LocalDateTime createdAt;
}
