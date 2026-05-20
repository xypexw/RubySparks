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

    // Tạo danh sách phát mới
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

    // Lấy danh sách phát theo ID
    public PlaylistDTO getPlaylistById(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return convertToDTO(playlist);
    }

    // Lấy danh sách phát của người dùng
    public List<PlaylistDTO> getUserPlaylists(UUID userId) {
        return playlistRepository.findByUserUserIdOrderByNameAsc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Thêm bài hát vào danh sách phát
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

    // Xóa bài hát khỏi danh sách phát
    @Transactional
    public void removeSongFromPlaylist(UUID playlistId, UUID songId) {
        PlaylistSongId playlistSongId = new PlaylistSongId(playlistId, songId);
        if (playlistSongRepository.existsById(playlistSongId)) {
            playlistSongRepository.deleteById(playlistSongId);
        }
    }

    // Lấy danh sách bài hát trong playlist
    public List<SongDTO> getPlaylistSongs(UUID playlistId) {
        List<PlaylistSong> playlistSongs = playlistSongRepository.findByIdPlaylistId(playlistId);
        return playlistSongs.stream()
                .map(ps -> songService.convertToDTO(ps.getSong()))
                .collect(Collectors.toList());
    }

    // Theo dõi danh sách phát
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

    // Bỏ theo dõi danh sách phát
    @Transactional
    public void unfollowPlaylist(UUID userId, UUID playlistId) {
        PlaylistFollowId playlistFollowId = new PlaylistFollowId(userId, playlistId);
        if (playlistFollowRepository.existsById(playlistFollowId)) {
            playlistFollowRepository.deleteById(playlistFollowId);
        }
    }

    // Lấy các danh sách phát đã theo dõi
    public List<PlaylistDTO> getFollowedPlaylists(UUID userId) {
        List<PlaylistFollow> follows = playlistFollowRepository.findByIdUserId(userId);
        return follows.stream()
                .map(f -> convertToDTO(f.getPlaylist()))
                .collect(Collectors.toList());
    }

    // Xóa danh sách phát
    @Transactional
    public void deletePlaylist(UUID playlistId) {
        playlistSongRepository.deleteByIdPlaylistId(playlistId);
        playlistFollowRepository.deleteByIdPlaylistId(playlistId);
        playlistRepository.deleteById(playlistId);
    }

    // Cập nhật thông tin danh sách phát
    @Transactional
    public PlaylistDTO updatePlaylist(UUID playlistId, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (playlistDTO.getName() != null && !playlistDTO.getName().trim().isEmpty()) {
            playlist.setName(playlistDTO.getName().trim());
        }
        if (playlistDTO.getStatus() != null) {
            playlist.setStatus(playlistDTO.getStatus());
        }
        Playlist saved = playlistRepository.save(playlist);
        return convertToDTO(saved);
    }

    // Loại bỏ dấu tiếng Việt để tìm kiếm
    private String removeAccents(String src) {
        if (src == null) return "";
        // Thay thế các ký tự lỗi encoding phổ biến của tiếng Việt sang nguyên âm gốc 'a'
        String clean = src.replace("\uFFFD", "a").replace("", "a");
        String nfdNormalizedString = java.text.Normalizer.normalize(clean, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(nfdNormalizedString).replaceAll("");
        return result.replace('đ', 'd').replace('Đ', 'D').toLowerCase().trim();
    }

    // Tìm kiếm danh sách phát công khai
    public List<PlaylistDTO> searchPublicPlaylists(String query) {
        if (query == null) return List.of();
        
        String cleanQuery = query.toLowerCase().trim();
        List<String> playlistKeywords = List.of(
            "playlist", "danh sách phát", "danh sach phat", "danh sách", "danh sach", 
            "tuyển tập", "tuyen tap", "tập hợp", "tap hop", "nghe tuyển", "nghe tuyen", "album", "list"
        );
        
        for (String keyword : playlistKeywords) {
            cleanQuery = cleanQuery.replace(keyword, "");
        }
        cleanQuery = cleanQuery.trim();
        
        List<Playlist> allPublicPlaylists = playlistRepository.findByNameContainingIgnoreCaseAndStatus("", "PUBLIC");
        
        if (cleanQuery.isEmpty()) {
            // Nếu người dùng chỉ gõ các từ chỉ định thực thể playlist, trả về tất cả playlist công khai làm gợi ý
            return allPublicPlaylists.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        String queryNormalized = removeAccents(cleanQuery);
        
        return allPublicPlaylists.stream()
                .filter(pl -> {
                    String nameNormalized = removeAccents(pl.getName());
                    return nameNormalized.contains(queryNormalized);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi entity Playlist sang PlaylistDTO
    public PlaylistDTO convertToDTO(Playlist playlist) {
        String coverUrl = null;
        int songCount = 0;
        try {
            List<PlaylistSong> playlistSongs = playlistSongRepository.findByIdPlaylistId(playlist.getPlaylistId());
            if (playlistSongs != null) {
                songCount = playlistSongs.size();
                if (!playlistSongs.isEmpty()) {
                    PlaylistSong lastAdded = playlistSongs.get(playlistSongs.size() - 1);
                    if (lastAdded.getSong() != null) {
                        coverUrl = lastAdded.getSong().getThumbnailUrl();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore and fall back to null
        }

        return PlaylistDTO.builder()
                .playlistId(playlist.getPlaylistId())
                .userId(playlist.getUser().getUserId())
                .name(playlist.getName())
                .status(playlist.getStatus())
                .createdAt(playlist.getCreatedAt())
                .coverUrl(coverUrl)
                .songCount(songCount)
                .build();
    }
}
