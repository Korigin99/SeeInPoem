package com.korigin.sip.dto.response;

import com.korigin.sip.entity.Poem;
import com.korigin.sip.entity.enums.Category;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PoemDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String authorNickname;
    private final Category category;
    private final int views;
    private final int likeCount;
    private final boolean isLiked;
    private final boolean isAuthor;
    private final List<CommentResponse> comments;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PoemDetailResponse(Poem poem, List<CommentResponse> comments, boolean isLiked, boolean isAuthor) {
        this.id = poem.getId();
        this.title = poem.getTitle();
        this.content = poem.getContent();
        this.authorNickname = poem.isAnonymous() ? "익명" : poem.getAuthor().getNickname();
        this.category = poem.getCategory();
        this.views = poem.getViews();
        this.likeCount = poem.getLikes().size();
        this.isLiked = isLiked;
        this.isAuthor = isAuthor;
        this.comments = comments;
        this.createdAt = poem.getCreatedAt();
        this.updatedAt = poem.getUpdatedAt();
    }
}
