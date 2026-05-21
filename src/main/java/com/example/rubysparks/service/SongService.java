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

    // Tạo bài hát mới
    @Transactional
    public SongDTO createSong(SongDTO songDTO) {
        if (songDTO.getItunesId() != null && !songDTO.getItunesId().trim().isEmpty()) {
            java.util.Optional<Song> existing = songRepository.findByItunesId(songDTO.getItunesId().trim());
            if (existing.isPresent()) {
                return convertToDTO(existing.get());
            }
        }

        String genreIdsStr = null;
        if (songDTO.getGenreIds() != null && !songDTO.getGenreIds().isEmpty()) {
            genreIdsStr = String.join(",", songDTO.getGenreIds());
        }

        // Mặc định cho LOCAL là PENDING, cho ITUNES là APPROVED
        String defaultStatus = "ITUNES".equalsIgnoreCase(songDTO.getSource()) ? "APPROVED" : "PENDING";

        Song song = Song.builder()
                .title(songDTO.getTitle())
                .artistName(songDTO.getArtistName())
                .source(songDTO.getSource())
                .itunesId(songDTO.getItunesId())
                .previewUrl(songDTO.getPreviewUrl())
                .fileUrl(songDTO.getFileUrl())
                .thumbnailUrl(songDTO.getThumbnailUrl())
                .duration(songDTO.getDuration())
                .status(songDTO.getStatus() != null ? songDTO.getStatus() : defaultStatus)
                .ownerUserId(songDTO.getOwnerUserId())
                .rejectReason(songDTO.getRejectReason())
                .description(songDTO.getDescription())
                .genreIds(genreIdsStr)
                .build();

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }

    // Lấy bài hát theo ID
    public SongDTO getSongById(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        return convertToDTO(song);
    }

    // Tìm kiếm bài hát theo từ khóa
    public List<SongDTO> searchSongs(String query) {
        List<Song> songs = songRepository.findByTitleContainingIgnoreCase(query);
        if (songs.isEmpty()) {
            songs = songRepository.findByArtistNameContainingIgnoreCase(query);
        }
        return songs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy tất cả bài hát
    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Thích bài hát
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

    // Bỏ thích bài hát
    @Transactional
    public void unlikeSong(UUID userId, UUID songId) {
        LikedSongId likedSongId = new LikedSongId(userId, songId);
        if (likedSongRepository.existsById(likedSongId)) {
            likedSongRepository.deleteById(likedSongId);
        }
    }

    // Ghi nhận lịch sử nghe nhạc
    @Transactional
    public void recordListenHistory(String userIdStr, UUID songId) {
        UUID userId = null;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            // Không phải UUID hợp lệ
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        if (user == null) {
            user = userRepository.findByEmail("user@gmail.com").orElse(null);
        }

        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElse(null);
        }

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        ListenHistory listenHistory = ListenHistory.builder()
                .user(user)
                .song(song)
                .listenedAt(LocalDateTime.now())
                .build();

        listenHistoryRepository.save(listenHistory);
    }

    // Lấy các bài hát đã thích của người dùng
    public List<SongDTO> getLikedSongs(UUID userId) {
        List<LikedSong> likedSongs = likedSongRepository.findByIdUserId(userId);
        return likedSongs.stream()
                .map(liked -> convertToDTO(liked.getSong()))
                .collect(Collectors.toList());
    }

    // Chuyển đổi entity Song sang SongDTO
    public SongDTO convertToDTO(Song song) {
        List<String> genreIdsList = null;
        if (song.getGenreIds() != null && !song.getGenreIds().trim().isEmpty()) {
            genreIdsList = java.util.Arrays.asList(song.getGenreIds().split(","));
        }

        long listenCount = listenHistoryRepository.countBySongSongId(song.getSongId());

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
                .ownerUserId(song.getOwnerUserId())
                .rejectReason(song.getRejectReason())
                .description(song.getDescription())
                .genreIds(genreIdsList)
                .createdAt(song.getCreatedAt())
                .listenCount(listenCount)
                .build();
    }

    // Cập nhật thông tin bài hát (Nghệ sĩ)
    @Transactional
    public SongDTO updateSong(UUID songId, SongDTO songDTO) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        song.setTitle(songDTO.getTitle());
        song.setArtistName(songDTO.getArtistName());
        song.setFileUrl(songDTO.getFileUrl());
        song.setThumbnailUrl(songDTO.getThumbnailUrl());
        song.setDuration(songDTO.getDuration());
        song.setDescription(songDTO.getDescription());

        if (songDTO.getGenreIds() != null) {
            song.setGenreIds(String.join(",", songDTO.getGenreIds()));
        } else {
            song.setGenreIds(null);
        }

        // Nếu bài hát từng bị REJECTED, chuyển lại thành PENDING và xóa rejectReason để duyệt lại
        if ("REJECTED".equalsIgnoreCase(song.getStatus())) {
            song.setStatus("PENDING");
            song.setRejectReason("");
        }

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }

    // Xóa bài hát (Nghệ sĩ)
    @Transactional
    public void deleteSong(UUID songId) {
        if (!songRepository.existsById(songId)) {
            throw new RuntimeException("Song not found");
        }
        songRepository.deleteById(songId);
    }

    // Cập nhật trạng thái duyệt bài hát (Admin)
    @Transactional
    public SongDTO updateStatus(UUID songId, String status, String rejectReason) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        song.setStatus(status.toUpperCase());
        if ("REJECTED".equalsIgnoreCase(status)) {
            song.setRejectReason(rejectReason != null ? rejectReason.trim() : "");
        } else {
            song.setRejectReason("");
        }

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }
}
