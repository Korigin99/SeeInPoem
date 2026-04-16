package com.korigin.sip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject("[시인시] 이메일 인증 코드");
            helper.setText(buildEmailBody(code), true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 발송 실패 - to: {}, error: {}", to, e.getMessage());
        }
    }

    private String buildEmailBody(String code) {
        return """
            <div style="max-width:480px;margin:0 auto;font-family:'Apple SD Gothic Neo',sans-serif;color:#1c1c1e">
              <div style="padding:40px 0 20px;text-align:center">
                <span style="font-size:28px;font-weight:700;color:#4F46E5">시인시</span>
              </div>
              <div style="background:#FAFAF8;border:1px solid #e4e4e7;border-radius:16px;padding:36px 40px">
                <h2 style="margin:0 0 8px;font-size:20px;font-weight:600">이메일 인증 코드</h2>
                <p style="margin:0 0 28px;font-size:14px;color:#6b7280">
                  아래 6자리 코드를 입력하여 이메일 인증을 완료해주세요.<br>
                  코드는 <strong>10분간</strong> 유효합니다.
                </p>
                <div style="background:#fff;border:2px solid #4F46E5;border-radius:12px;padding:24px;text-align:center">
                  <span style="font-size:40px;font-weight:700;letter-spacing:12px;color:#4F46E5">%s</span>
                </div>
                <p style="margin:24px 0 0;font-size:12px;color:#9ca3af;text-align:center">
                  본인이 요청하지 않은 경우 이 이메일을 무시하세요.
                </p>
              </div>
            </div>
            """.formatted(code);
    }
}
