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

    // Tạo thể loại mới
    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreDTO genreDTO) {
        return ResponseEntity.ok(genreService.createGenre(genreDTO));
    }

    // Lấy tất cả thể loại
    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    // Lấy thể loại theo ID
    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable UUID genreId) {
        return ResponseEntity.ok(genreService.getGenreById(genreId));
    }

    // Thêm bài hát vào thể loại
    @PostMapping("/{genreId}/songs/{songId}")
    public ResponseEntity<Void> addSongToGenre(@PathVariable UUID genreId, @PathVariable UUID songId) {
        genreService.addSongToGenre(genreId, songId);
        return ResponseEntity.ok().build();
    }

    // Lấy danh sách bài hát theo thể loại
    @GetMapping("/{genreId}/songs")
    public ResponseEntity<List<SongDTO>> getSongsByGenre(@PathVariable UUID genreId) {
        return ResponseEntity.ok(genreService.getSongsByGenre(genreId));
    }
}
