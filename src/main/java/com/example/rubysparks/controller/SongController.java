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

    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable UUID songId) {
        return ResponseEntity.ok(songService.getSongById(songId));
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> searchOrGetAllSongs(@RequestParam(required = false) String query) {
        if (query != null && !query.trim().isEmpty()) {
            return ResponseEntity.ok(songService.searchSongs(query));
        }
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @PostMapping("/{songId}/like")
    public ResponseEntity<Void> likeSong(@PathVariable UUID songId, @RequestParam UUID userId) {
        songService.likeSong(userId, songId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{songId}/unlike")
    public ResponseEntity<Void> unlikeSong(@PathVariable UUID songId, @RequestParam UUID userId) {
        songService.unlikeSong(userId, songId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{songId}/listen")
    public ResponseEntity<Void> recordListenHistory(@PathVariable UUID songId, @RequestParam UUID userId) {
        songService.recordListenHistory(userId, songId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liked")
    public ResponseEntity<List<SongDTO>> getLikedSongs(@RequestParam UUID userId) {
        return ResponseEntity.ok(songService.getLikedSongs(userId));
    }
}
