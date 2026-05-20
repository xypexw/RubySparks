package com.example.rubysparks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String username;
    private String stageName;
    private String avatarUrl;
    private String currentPassword;
    private String newPassword;
}
