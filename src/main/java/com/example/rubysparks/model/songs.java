package com.example.rubysparks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class songs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long songId;
    private String title;
    private String artistName;
    private String imageUrl;
    private LocalDateTime createTime;
    private String itunesId;
    private String fileUrl;
    private String previewUrl;
    private int duration;



    private Long playlistId;
}
