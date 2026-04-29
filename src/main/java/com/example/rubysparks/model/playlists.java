package com.example.rubysparks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class playlists {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime createTime;
    private Long userId;
    private Long musicId;

}
