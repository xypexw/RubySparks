package com.example.rubysparks.config;

import com.example.rubysparks.model.User;
import com.example.rubysparks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUser("admin", "admin@gmail.com", "ADMIN", null);
        seedUser("user", "user@gmail.com", "USER", null);
        seedUser("artist", "artist@gmail.com", "ARTIST", "Artist Demo");
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
}
