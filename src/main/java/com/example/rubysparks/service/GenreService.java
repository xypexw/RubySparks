package com.example.rubysparks.service;

import com.example.rubysparks.dto.GenreDTO;
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
public class GenreService {

    private final GenreRepository genreRepository;
    private final SongRepository songRepository;
    private final GenreSongRepository genreSongRepository;
    private final SongService songService;

    @Transactional
    public GenreDTO createGenre(GenreDTO genreDTO) {
        if (genreRepository.existsByName(genreDTO.getName())) {
            throw new RuntimeException("Genre name already exists");
        }

        Genre genre = Genre.builder()
                .name(genreDTO.getName())
                .build();

        Genre savedGenre = genreRepository.save(genre);
        return convertToDTO(savedGenre);
    }

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public GenreDTO getGenreById(UUID genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        return convertToDTO(genre);
    }

    @Transactional
    public void addSongToGenre(UUID genreId, UUID songId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        GenreSongId genreSongId = new GenreSongId(genreId, songId);
        if (genreSongRepository.existsById(genreSongId)) {
            return; // Already associated
        }

        GenreSong genreSong = GenreSong.builder()
                .id(genreSongId)
                .genre(genre)
                .song(song)
                .createdAt(LocalDateTime.now())
                .build();

        genreSongRepository.save(genreSong);
    }

    public List<SongDTO> getSongsByGenre(UUID genreId) {
        List<GenreSong> genreSongs = genreSongRepository.findByIdGenreId(genreId);
        return genreSongs.stream()
                .map(gs -> songService.convertToDTO(gs.getSong()))
                .collect(Collectors.toList());
    }

    public GenreDTO convertToDTO(Genre genre) {
        return GenreDTO.builder()
                .genreId(genre.getGenreId())
                .name(genre.getName())
                .createdAt(genre.getCreatedAt())
                .build();
    }
}
