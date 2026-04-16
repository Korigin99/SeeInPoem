package com.korigin.sip.dto.response;

import com.korigin.sip.entity.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserProfileResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final int poemCount;
    private final LocalDateTime createdAt;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.poemCount = user.getPoems().size();
        this.createdAt = user.getCreatedAt();
    }
}
