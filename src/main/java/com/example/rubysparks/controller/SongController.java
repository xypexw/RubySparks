package com.example.rubysparks.controller;

import com.example.rubysparks.dto.SongDTO;
import com.example.rubysparks.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SongController {

    private final SongService songService;

    // Tạo bài hát mới
    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    // Lấy chi tiết bài hát theo ID
    @GetMapping("/{songId}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable UUID songId) {
        return ResponseEntity.ok(songService.getSongById(songId));
    }

    // Tìm kiếm hoặc lấy tất cả bài hát
    @GetMapping
    public ResponseEntity<List<SongDTO>> searchOrGetAllSongs(@RequestParam(required = false) String query) {
        if (query != null && !query.trim().isEmpty()) {
            return ResponseEntity.ok(songService.searchSongs(query));
        }
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // Thích bài hát
    @PostMapping("/{songId}/like")
    public ResponseEntity<Void> likeSong(@PathVariable UUID songId, @RequestParam UUID userId) {
        songService.likeSong(userId, songId);
        return ResponseEntity.ok().build();
    }

    // Bỏ thích bài hát
    @PostMapping("/{songId}/unlike")
    public ResponseEntity<Void> unlikeSong(@PathVariable UUID songId, @RequestParam UUID userId) {
        songService.unlikeSong(userId, songId);
        return ResponseEntity.ok().build();
    }

    // Ghi nhận lịch sử nghe nhạc
    @PostMapping("/{songId}/listen")
    public ResponseEntity<Void> recordListenHistory(@PathVariable UUID songId, @RequestParam String userId) {
        songService.recordListenHistory(userId, songId);
        return ResponseEntity.ok().build();
    }

    // Lấy danh sách các bài hát đã thích
    @GetMapping("/liked")
    public ResponseEntity<List<SongDTO>> getLikedSongs(@RequestParam UUID userId) {
        return ResponseEntity.ok(songService.getLikedSongs(userId));
    }

    // Cập nhật thông tin bài hát (Nghệ sĩ)
    @PutMapping("/{songId}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable UUID songId, @RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.updateSong(songId, songDTO));
    }

    // Xóa bài hát (Nghệ sĩ)
    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable UUID songId) {
        songService.deleteSong(songId);
        return ResponseEntity.ok().build();
    }

    // Cập nhật trạng thái duyệt bài hát (Admin)
    @PutMapping("/{songId}/status")
    public ResponseEntity<SongDTO> updateStatus(
            @PathVariable UUID songId,
            @RequestParam String status,
            @RequestParam(required = false) String rejectReason) {
        return ResponseEntity.ok(songService.updateStatus(songId, status, rejectReason));
    }
}
