package com.example.rubysparks.controller;

import com.example.rubysparks.dto.*;
import com.example.rubysparks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // Đăng ký người dùng mới
    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    // Đăng nhập
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // Lấy thông tin cá nhân của người dùng
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/users/{userId}/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable UUID userId,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // Yêu cầu gửi mã OTP quên mật khẩu
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    // Đặt lại mật khẩu bằng mã OTP
    @PostMapping("/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
