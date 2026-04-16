package com.korigin.sip.security;

import com.korigin.sip.entity.RefreshToken;
import com.korigin.sip.entity.User;
import com.korigin.sip.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oauth2User.getUser();

        String accessToken = jwtTokenProvider.generateToken(user.getEmail());
        String rawRefreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.save(RefreshToken.builder()
            .token(rawRefreshToken)
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build());

        String redirectUrl = frontendUrl + "/oauth2/callback"
            + "?token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
            + "&refreshToken=" + URLEncoder.encode(rawRefreshToken, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
