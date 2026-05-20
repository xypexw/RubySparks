package com.example.rubysparks.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSchemaUpdater implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Checking for unique constraints on users(username)...");
            // Query to find unique indexes on 'username' column in 'users' table in current database
            String sql = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS " +
                         "WHERE TABLE_SCHEMA = DATABASE() " +
                         "AND TABLE_NAME = 'users' " +
                         "AND COLUMN_NAME = 'username' " +
                         "AND NON_UNIQUE = 0";
            List<String> indexNames = jdbcTemplate.queryForList(sql, String.class);
            for (String indexName : indexNames) {
                if ("PRIMARY".equalsIgnoreCase(indexName)) {
                    continue; // Skip primary key
                }
                log.info("Dropping unique index '{}' on users(username) to allow duplicate display names...", indexName);
                jdbcTemplate.execute("ALTER TABLE users DROP INDEX " + indexName);
                log.info("Successfully dropped unique index '{}'.", indexName);
            }
        } catch (Exception e) {
            log.error("Failed to drop unique constraint on users(username): {}", e.getMessage());
        }
    }
}
