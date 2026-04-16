package com.korigin.sip.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
