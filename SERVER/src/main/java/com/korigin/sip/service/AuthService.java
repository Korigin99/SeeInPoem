package com.korigin.sip.service;

import com.korigin.sip.dto.request.RefreshTokenRequest;
import com.korigin.sip.dto.response.AuthResponse;
import com.korigin.sip.entity.RefreshToken;
import com.korigin.sip.entity.User;
import com.korigin.sip.exception.CustomException;
import com.korigin.sip.exception.ErrorCode;
import com.korigin.sip.repository.RefreshTokenRepository;
import com.korigin.sip.repository.UserRepository;
import com.korigin.sip.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        User user = stored.getUser();
        refreshTokenRepository.delete(stored);

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String rawRefreshToken = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
            .token(rawRefreshToken)
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS))
            .build());

        return new AuthResponse(accessToken, rawRefreshToken, user.getNickname(), user.getEmail());
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteAllByUserId(user.getId());
    }
}
