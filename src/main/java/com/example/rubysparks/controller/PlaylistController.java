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

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.ok(playlistService.createPlaylist(playlistDTO));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistById(playlistId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaylistDTO>> getUserPlaylists(@PathVariable UUID userId) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable UUID playlistId, @PathVariable UUID songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<SongDTO>> getPlaylistSongs(@PathVariable UUID playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistSongs(playlistId));
    }

    @PostMapping("/{playlistId}/follow")
    public ResponseEntity<Void> followPlaylist(@PathVariable UUID playlistId, @RequestParam UUID userId) {
        playlistService.followPlaylist(userId, playlistId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playlistId}/unfollow")
    public ResponseEntity<Void> unfollowPlaylist(@PathVariable UUID playlistId, @RequestParam UUID userId) {
        playlistService.unfollowPlaylist(userId, playlistId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followed")
    public ResponseEntity<List<PlaylistDTO>> getFollowedPlaylists(@RequestParam UUID userId) {
        return ResponseEntity.ok(playlistService.getFollowedPlaylists(userId));
    }
}
