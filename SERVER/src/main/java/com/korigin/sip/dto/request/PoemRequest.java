package com.korigin.sip.dto.request;

import com.korigin.sip.entity.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PoemRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private Category category;

    private boolean anonymous;
}
