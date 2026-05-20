package com.example.rubysparks.service;

import com.example.rubysparks.dto.PlaylistDTO;
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
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistFollowRepository playlistFollowRepository;
    private final SongService songService;

    @Transactional
    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        User user = userRepository.findById(playlistDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = Playlist.builder()
                .user(user)
                .name(playlistDTO.getName())
                .status("PUBLIC")
                .build();

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    public PlaylistDTO getPlaylistById(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return convertToDTO(playlist);
    }

    public List<PlaylistDTO> getUserPlaylists(UUID userId) {
        return playlistRepository.findByUserUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addSongToPlaylist(UUID playlistId, UUID songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);
        if (playlistSongRepository.existsById(playlistSongId)) {
            return; // Already in playlist
        }

        PlaylistSong playlistSong = PlaylistSong.builder()
                .id(playlistSongId)
                .playlist(playlist)
                .song(song)
                .addedAt(LocalDateTime.now())
                .build();

        playlistSongRepository.save(playlistSong);
    }

    @Transactional
    public void removeSongFromPlaylist(UUID playlistId, UUID songId) {
        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);
        if (playlistSongRepository.existsById(playlistSongId)) {
            playlistSongRepository.deleteById(playlistSongId);
        }
    }

    public List<SongDTO> getPlaylistSongs(UUID playlistId) {
        List<PlaylistSong> playlistSongs = playlistSongRepository.findByIdPlaylistId(playlistId);
        return playlistSongs.stream()
                .map(ps -> songService.convertToDTO(ps.getSong()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void followPlaylist(UUID userId, UUID playlistId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        PlaylistFollowId playlistFollowId = new PlaylistFollowId(userId, playlistId);
        if (playlistFollowRepository.existsById(playlistFollowId)) {
            return; // Already following
        }

        PlaylistFollow playlistFollow = PlaylistFollow.builder()
                .id(playlistFollowId)
                .user(user)
                .playlist(playlist)
                .createdAt(LocalDateTime.now())
                .build();

        playlistFollowRepository.save(playlistFollow);
    }

    @Transactional
    public void unfollowPlaylist(UUID userId, UUID playlistId) {
        PlaylistFollowId playlistFollowId = new PlaylistFollowId(userId, playlistId);
        if (playlistFollowRepository.existsById(playlistFollowId)) {
            playlistFollowRepository.deleteById(playlistFollowId);
        }
    }

    public List<PlaylistDTO> getFollowedPlaylists(UUID userId) {
        List<PlaylistFollow> follows = playlistFollowRepository.findByIdUserId(userId);
        return follows.stream()
                .map(f -> convertToDTO(f.getPlaylist()))
                .collect(Collectors.toList());
    }

    public PlaylistDTO convertToDTO(Playlist playlist) {
        return PlaylistDTO.builder()
                .playlistId(playlist.getPlaylistId())
                .userId(playlist.getUser().getUserId())
                .name(playlist.getName())
                .status(playlist.getStatus())
                .createdAt(playlist.getCreatedAt())
                .build();
    }
}
