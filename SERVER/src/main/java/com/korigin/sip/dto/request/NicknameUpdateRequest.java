package com.korigin.sip.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NicknameUpdateRequest {

    @NotBlank
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    private String nickname;
}
