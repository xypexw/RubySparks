package com.example.rubysparks.controller;

import com.example.rubysparks.dto.GenreDTO;
import com.example.rubysparks.dto.SongDTO;
import com.example.rubysparks.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreDTO genreDTO) {
        return ResponseEntity.ok(genreService.createGenre(genreDTO));
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable UUID genreId) {
        return ResponseEntity.ok(genreService.getGenreById(genreId));
    }

    @PostMapping("/{genreId}/songs/{songId}")
    public ResponseEntity<Void> addSongToGenre(@PathVariable UUID genreId, @PathVariable UUID songId) {
        genreService.addSongToGenre(genreId, songId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{genreId}/songs")
    public ResponseEntity<List<SongDTO>> getSongsByGenre(@PathVariable UUID genreId) {
        return ResponseEntity.ok(genreService.getSongsByGenre(genreId));
    }
}
