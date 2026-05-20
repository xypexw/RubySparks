package com.example.rubysparks.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtils {
    // Secret key for HMAC-SHA256 (must be at least 256 bits / 32 bytes)
    private static final String SECRET_KEY = "melono-super-secret-key-32-bytes-long-for-hs256!";

    /**
     * Generates a standard HS256 JWT token for the given username and role.
     *
     * @param username the username of the user
     * @param role the role of the user (e.g. USER, ARTIST, ADMIN)
     * @return a valid signed JWT string
     */
    public static String generateToken(String username, String role) {
        try {
            // 1. JWT Header
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));

            // 2. JWT Payload
            long now = System.currentTimeMillis() / 1000;
            long exp = now + 86400 * 7; // Token valid for 7 days
            String payload = String.format(
                "{\"sub\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                username, role, now, exp
            );
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            // 3. Signature
            String signatureInput = encodedHeader + "." + encodedPayload;
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(signatureInput.getBytes(StandardCharsets.UTF_8));
            String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Complete standard JWT
            return encodedHeader + "." + encodedPayload + "." + encodedSignature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }
}
