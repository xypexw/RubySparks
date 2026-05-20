package com.example.rubysparks.controller;

import com.example.rubysparks.dto.PlaylistDTO;
import com.example.rubysparks.dto.SongDTO;
import com.example.rubysparks.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaylistController {

    private final PlaylistService playlistService;

    // Tạo danh sách phát mới
    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.ok(playlistService.createPlaylist(playlistDTO));
    }

    // Lấy chi tiết danh sách phát theo ID
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistById(playlistId));
    }

    // Lấy danh sách phát của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistDTO>> getUserPlaylists(@PathVariable UUID userId) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    // Thêm bài hát vào danh sách phát
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    // Xóa bài hát khỏi danh sách phát
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    // Lấy danh sách bài hát trong danh sách phát
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<SongDTO>> getPlaylistSongs(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistSongs(playlistId));
    }

    // Theo dõi danh sách phát
    @PostMapping("/{playlistId}/follow")
    public ResponseEntity<Void> followPlaylist(@PathVariable UUID playlistId, @RequestParam UUID userId) {
        playlistService.followPlaylist(userId, playlistId);
        return ResponseEntity.ok().build();
    }

    // Bỏ theo dõi danh sách phát
    @PostMapping("/{playlistId}/unfollow")
    public ResponseEntity<Void> unfollowPlaylist(@PathVariable UUID playlistId, @RequestParam UUID userId) {
        playlistService.unfollowPlaylist(userId, playlistId);
        return ResponseEntity.ok().build();
    }

    // Lấy các danh sách phát đã theo dõi của người dùng
    @GetMapping("/followed")
    public ResponseEntity<List<PlaylistDTO>> getFollowedPlaylists(@RequestParam UUID userId) {
        return ResponseEntity.ok(playlistService.getFollowedPlaylists(userId));
    }

    // Tìm kiếm danh sách phát công khai
    @GetMapping("/search/public")
    public ResponseEntity<List<PlaylistDTO>> searchPublicPlaylists(@RequestParam String q) {
        return ResponseEntity.ok(playlistService.searchPublicPlaylists(q));
    }

    // Xóa danh sách phát
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable UUID playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.ok().build();
    }

    // Cập nhật thông tin danh sách phát
    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable UUID playlistId, @RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.ok(playlistService.updatePlaylist(playlistId, playlistDTO));
    }
}
