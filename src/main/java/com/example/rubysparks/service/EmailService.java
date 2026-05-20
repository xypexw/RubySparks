package com.example.rubysparks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Melono Music <no-reply@melono.com>");
            message.setTo(toEmail);
            message.setSubject("Mã xác thực khôi phục mật khẩu - Melono");
            message.setText("Chào bạn,\n\nMã xác thực OTP khôi phục mật khẩu của bạn là: " + code 
                    + "\n\nMã này có hiệu lực trong vòng 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\nTrân trọng,\nĐội ngũ Melono");
            mailSender.send(message);
            log.info("Email OTP đã gửi thành công tới: {}", toEmail);
        } catch (Exception e) {
            log.error("Không thể gửi email thực tế (do chưa cấu hình SMTP). Chi tiết lỗi: {}", e.getMessage());
            log.warn("==================================================");
            log.warn("MÃ XÁC THỰC OTP CỦA BẠN (LOCAL DEVELOPMENT): [{}]", code);
            log.warn("==================================================");
        }
    }
}
