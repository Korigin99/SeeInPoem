package com.korigin.sip.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String nickname;
    private String email;
}
