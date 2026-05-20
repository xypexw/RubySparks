package com.example.rubysparks.service;

import com.example.rubysparks.dto.*;
import com.example.rubysparks.model.User;
import com.example.rubysparks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    // Cache in-memory lưu trữ OTP (email -> OtpSession)
    private final ConcurrentHashMap<String, OtpSession> otpCache = new ConcurrentHashMap<>();

    private static class OtpSession {
        private final String code;
        private final LocalDateTime expiresAt;

        public OtpSession(String code, int durationMinutes) {
            this.code = code;
            this.expiresAt = LocalDateTime.now().plusMinutes(durationMinutes);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        public String getCode() {
            return code;
        }
    }

    // Đăng ký người dùng mới
    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (request.getEmail() == null || !request.getEmail().matches(EMAIL_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Định dạng email không hợp lệ.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại.");
        }

        // username là Tên hiển thị (Display Name) của người dùng
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = request.getEmail().split("@")[0];
        }

        User user = User.builder()
                .username(username.trim())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Đăng nhập
    public AuthResponse login(LoginRequest request) {
        String email = request.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai địa chỉ email hoặc mật khẩu."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai địa chỉ email hoặc mật khẩu.");
        }

        // Generate a real standard JWT token
        String jwtToken = com.example.rubysparks.util.JwtUtils.generateToken(user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(jwtToken)
                .user(convertToDTO(user))
                .build();
    }

    // Lấy thông tin người dùng theo ID
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
        return convertToDTO(user);
    }

    // Yêu cầu gửi mã OTP quên mật khẩu
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng cung cấp địa chỉ Email.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Định dạng email không hợp lệ.");
        }

        // Tìm user theo email hoặc username nhập vào
        User user = userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không tồn tại trên hệ thống."));

        // Tạo mã xác thực ngẫu nhiên gồm 6 chữ số
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Lưu OTP vào cache (hiệu lực 5 phút)
        otpCache.put(user.getEmail(), new OtpSession(code, 5));

        // Gửi OTP qua email (có kèm fallback in log ở EmailService)
        emailService.sendVerificationCode(user.getEmail(), code);
    }

    // Đặt lại mật khẩu bằng mã OTP
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getNewPassword();

        if (email == null || code == null || newPassword == null || newPassword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng cung cấp đầy đủ thông tin.");
        }

        // Tìm user tương ứng
        User user = userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không tồn tại trên hệ thống."));

        // Kiểm tra OTP trong cache
        OtpSession session = otpCache.get(user.getEmail());
        if (session == null || session.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng lấy mã mới.");
        }

        if (!session.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã xác thực OTP không chính xác.");
        }

        // Mã hóa mật khẩu mới và lưu lại
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa OTP khỏi cache sau khi đổi mật khẩu thành công
        otpCache.remove(user.getEmail());
    }

    // Cập nhật thông tin cá nhân
    @Transactional
    public UserDTO updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng."));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername().trim());
        }
        if (request.getStageName() != null) {
            user.setStageName(request.getStageName().trim());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        // Nếu người dùng muốn đổi mật khẩu
        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mật khẩu hiện tại.");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không chính xác.");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Chuyển đổi entity User sang UserDTO
    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .stageName(user.getStageName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
