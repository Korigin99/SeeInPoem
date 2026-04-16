package com.korigin.sip.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerifyRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}
