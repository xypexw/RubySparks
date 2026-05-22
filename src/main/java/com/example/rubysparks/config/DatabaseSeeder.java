package com.example.rubysparks.config;

import com.example.rubysparks.model.*;
import com.example.rubysparks.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final GenreSongRepository genreSongRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed users
        seedUser("admin", "admin@gmail.com", "ADMIN", null);
        seedUser("user", "user@gmail.com", "USER", null);
        seedUser("artist", "artist@gmail.com", "ARTIST", "Artist Demo");

        // 2. Fetch seeded artist user
        UUID artistId = userRepository.findByEmail("artist@gmail.com")
                .map(User::getUserId)
                .orElse(null);

        if (artistId == null) {
            log.warn("Seeded artist user not found. Song owner IDs will be null.");
        }

        // 3. Seed beautiful local tracks
        seedSong("Chìm Sâu", "RPT MCK", "Rap", "1", "photo-1465847899084-d164df4dedc6", "02:35", artistId);
        seedSong("Nàng Thơ", "Hoàng Dũng", "Ballad", "2", "photo-1493225457124-a3eb161ffa5f", "04:12", artistId);
        seedSong("Lạ Lùng", "Vũ", "Acoustic", "3", "photo-1493225457124-a3eb161ffa5f", "04:22", artistId); // Fixed image
        seedSong("Có Em", "Madihu", "Pop", "4", "photo-1511671782779-c97d3d27a1d4", "03:10", artistId);
        seedSong("Mascara", "Chillies", "Ballad", "5", "photo-1511671782779-c97d3d27a1d4", "03:58", artistId);
        seedSong("Bước Qua Mùa Cô Đơn", "Vũ", "Ballad", "6", "photo-1453090927415-5f45085b65c0", "04:38", artistId);
        seedSong("Tháng Năm", "Soobin Hoàng Sơn", "Pop", "7", "photo-1516450360452-9312f5e86fc7", "03:45", artistId);
        seedSong("Bao Tiền Một Mớ Mớ Tình?", "tlinh", "Rap", "8", "photo-1518609878373-06d740f60d8b", "03:02", artistId);
        seedSong("Đã Lỡ Yêu Em Nhiều", "JustaTee", "Pop", "9", "photo-1506157786151-b8491531f063", "04:18", artistId);
        seedSong("Có Chàng Trai Viết Lên Cây", "Phan Mạnh Quỳnh", "Ballad", "10", "photo-1510915361894-db8b60106cb1", "05:05", artistId);
        seedSong("Muộn Rồi Mà Sao Còn", "Sơn Tùng M-TP", "Pop", "11", "photo-1482440308425-276ad0f28b19", "04:35", artistId);
        seedSong("Chúng Ta Của Hiện Tại", "Sơn Tùng M-TP", "Ballad", "12", "photo-1501386761578-eac5c94b800a", "05:01", artistId);
        seedSong("Đi Về Nhà", "Đen Vâu, JustaTee", "Rap", "13", "photo-1514525253161-7a46d19cd819", "03:22", artistId);
        seedSong("Bài Này Chill Phết", "Đen Vâu, Min", "Rap", "14", "photo-1470225620780-dba8ba36b745", "04:36", artistId);
        seedSong("Hai Triệu Năm", "Đen Vâu", "Rap", "15", "photo-1459749411175-04bf5292ceea", "03:37", artistId);
        seedSong("Ngày Đầu Tiên", "Đức Phúc", "Pop", "16", "photo-1514525253161-7a46d19cd819", "03:25", artistId); // Fixed image
    }

    private void seedUser(String username, String email, String role, String stageName) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("123456"))
                    .role(role)
                    .stageName(stageName)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            log.info("Successfully seeded user: {} with role: {}", email, role);
        } else {
            log.info("User {} already exists, skipping seed.", email);
        }
    }

    private Genre seedGenre(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> {
                    Genre genre = Genre.builder()
                            .name(name)
                            .createdAt(LocalDateTime.now())
                            .build();
                    Genre saved = genreRepository.save(genre);
                    log.info("Successfully seeded genre: {}", name);
                    return saved;
                });
    }

    private void seedSong(
            String title,
            String artistName,
            String genreName,
            String soundHelixNum,
            String unsplashPhotoId,
            String duration,
            UUID ownerUserId
    ) {
        java.util.Optional<Song> existingSongOpt = songRepository.findByTitleAndArtistNameAndSource(title, artistName, "LOCAL");
        String thumbnailUrl = "https://images.unsplash.com/" + unsplashPhotoId + "?w=500&auto=format&fit=crop&q=60";

        if (!existingSongOpt.isPresent()) {
            Genre genre = seedGenre(genreName);
            String genreIdsStr = genre.getGenreId().toString();

            String previewUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-" + soundHelixNum + ".mp3";
            String fileUrl = previewUrl;

            Song song = Song.builder()
                    .title(title)
                    .artistName(artistName)
                    .source("LOCAL")
                    .previewUrl(previewUrl)
                    .fileUrl(fileUrl)
                    .thumbnailUrl(thumbnailUrl)
                    .duration(duration)
                    .status("APPROVED")
                    .ownerUserId(ownerUserId)
                    .genreIds(genreIdsStr)
                    .createdAt(LocalDateTime.now())
                    .build();

            Song savedSong = songRepository.save(song);

            // Seed relational mapping in genre_songs
            GenreSongId genreSongId = new GenreSongId(genre.getGenreId(), savedSong.getSongId());
            GenreSong genreSong = GenreSong.builder()
                    .id(genreSongId)
                    .genre(genre)
                    .song(savedSong)
                    .createdAt(LocalDateTime.now())
                    .build();
            genreSongRepository.save(genreSong);

            log.info("Successfully seeded song: {} by {}", title, artistName);
        } else {
            Song existingSong = existingSongOpt.get();
            if (!thumbnailUrl.equals(existingSong.getThumbnailUrl())) {
                existingSong.setThumbnailUrl(thumbnailUrl);
                songRepository.save(existingSong);
                log.info("Successfully updated thumbnail URL for existing song: {} by {}", title, artistName);
            } else {
                log.info("Song: {} by {} already exists, skipping.", title, artistName);
            }
        }
    }
}
