package com.korigin.sip.security;

import com.korigin.sip.entity.User;
import com.korigin.sip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(request);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String socialId = String.valueOf(attributes.get("id"));

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String email = kakaoAccount.containsKey("email") ? (String) kakaoAccount.get("email") : null;

        // 이메일 미동의 시 합성 이메일 사용
        String effectiveEmail = (email != null && !email.isBlank())
            ? email
            : "kakao_" + socialId + "@kakao.social";

        User user = userRepository.findBySocialId(socialId)
            .orElseGet(() -> registerNewUser(socialId, effectiveEmail, nickname));

        // 닉네임 동기화 (카카오에서 변경된 경우)
        if (!user.getNickname().equals(nickname) && !userRepository.existsByNickname(nickname)) {
            user.setNickname(nickname);
        }

        return new CustomOAuth2User(user, attributes);
    }

    private User registerNewUser(String socialId, String email, String baseNickname) {
        // 이메일 중복 시 기존 계정에 소셜 연동
        return userRepository.findByEmail(email)
            .map(existing -> {
                existing.setSocialId(socialId);
                existing.setProvider("KAKAO");
                return existing;
            })
            .orElseGet(() -> {
                String nickname = resolveUniqueNickname(baseNickname);
                User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .provider("KAKAO")
                    .socialId(socialId)
                    .build();
                return userRepository.save(newUser);
            });
    }

    private String resolveUniqueNickname(String base) {
        if (!userRepository.existsByNickname(base)) return base;
        int suffix = 2;
        while (userRepository.existsByNickname(base + suffix)) suffix++;
        return base + suffix;
    }
}
