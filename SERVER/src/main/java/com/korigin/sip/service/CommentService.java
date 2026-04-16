package com.korigin.sip.service;

import com.korigin.sip.dto.request.CommentRequest;
import com.korigin.sip.dto.request.CommentUpdateRequest;
import com.korigin.sip.dto.response.CommentResponse;
import com.korigin.sip.entity.Comment;
import com.korigin.sip.entity.Poem;
import com.korigin.sip.entity.User;
import com.korigin.sip.exception.CustomException;
import com.korigin.sip.exception.ErrorCode;
import com.korigin.sip.repository.CommentRepository;
import com.korigin.sip.repository.PoemRepository;
import com.korigin.sip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PoemRepository poemRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(String email, Long poemId, CommentRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Poem poem = poemRepository.findById(poemId)
            .orElseThrow(() -> new CustomException(ErrorCode.POEM_NOT_FOUND));
        Comment comment = Comment.builder()
            .content(request.getContent())
            .author(user)
            .poem(poem)
            .build();
        return new CommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse updateComment(String email, Long commentId, CommentUpdateRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        comment.setContent(request.getContent());
        return new CommentResponse(comment);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        commentRepository.delete(comment);
    }
}
