package com.example.rubysparks.service;

import com.example.rubysparks.dto.SongDTO;
import com.example.rubysparks.model.*;
import com.example.rubysparks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final LikedSongRepository likedSongRepository;
    private final ListenHistoryRepository listenHistoryRepository;

    @Transactional
    public SongDTO createSong(SongDTO songDTO) {
        Song song = Song.builder()
                .title(songDTO.getTitle())
                .artistName(songDTO.getArtistName())
                .source(songDTO.getSource())
                .itunesId(songDTO.getItunesId())
                .previewUrl(songDTO.getPreviewUrl())
                .fileUrl(songDTO.getFileUrl())
                .thumbnailUrl(songDTO.getThumbnailUrl())
                .duration(songDTO.getDuration())
                .status("ACTIVE")
                .build();

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }

    public SongDTO getSongById(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        return convertToDTO(song);
    }

    public List<SongDTO> searchSongs(String query) {
        List<Song> songs = songRepository.findByTitleContainingIgnoreCase(query);
        if (songs.isEmpty()) {
            songs = songRepository.findByArtistNameContainingIgnoreCase(query);
        }
        return songs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void likeSong(UUID userId, UUID songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        LikedSongId likedSongId = new LikedSongId(userId, songId);
        if (likedSongRepository.existsById(likedSongId)) {
            return; // Already liked
        }

        LikedSong likedSong = LikedSong.builder()
                .id(likedSongId)
                .user(user)
                .song(song)
                .createdAt(LocalDateTime.now())
                .build();

        likedSongRepository.save(likedSong);
    }

    @Transactional
    public void unlikeSong(UUID userId, UUID songId) {
        LikedSongId likedSongId = new LikedSongId(userId, songId);
        if (likedSongRepository.existsById(likedSongId)) {
            likedSongRepository.deleteById(likedSongId);
        }
    }

    @Transactional
    public void recordListenHistory(UUID userId, UUID songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        ListenHistory listenHistory = ListenHistory.builder()
                .user(user)
                .song(song)
                .listenedAt(LocalDateTime.now())
                .build();

        listenHistoryRepository.save(listenHistory);
    }

    public List<SongDTO> getLikedSongs(UUID userId) {
        List<LikedSong> likedSongs = likedSongRepository.findByIdUserId(userId);
        return likedSongs.stream()
                .map(liked -> convertToDTO(liked.getSong()))
                .collect(Collectors.toList());
    }

    public SongDTO convertToDTO(Song song) {
        return SongDTO.builder()
                .songId(song.getSongId())
                .title(song.getTitle())
                .artistName(song.getArtistName())
                .source(song.getSource())
                .itunesId(song.getItunesId())
                .previewUrl(song.getPreviewUrl())
                .fileUrl(song.getFileUrl())
                .thumbnailUrl(song.getThumbnailUrl())
                .duration(song.getDuration())
                .status(song.getStatus())
                .createdAt(song.getCreatedAt())
                .build();
    }
}
