package com.korigin.sip.dto.response;

import com.korigin.sip.entity.Poem;
import com.korigin.sip.entity.enums.Category;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PoemSummaryResponse {

    private final Long id;
    private final String title;
    private final String authorNickname;
    private final Category category;
    private final int views;
    private final int likeCount;
    private final int commentCount;
    private final LocalDateTime createdAt;

    public PoemSummaryResponse(Poem poem) {
        this.id = poem.getId();
        this.title = poem.getTitle();
        this.authorNickname = poem.isAnonymous() ? "익명" : poem.getAuthor().getNickname();
        this.category = poem.getCategory();
        this.views = poem.getViews();
        this.likeCount = poem.getLikes().size();
        this.commentCount = poem.getComments().size();
        this.createdAt = poem.getCreatedAt();
    }
}
