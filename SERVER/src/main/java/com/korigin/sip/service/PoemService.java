package com.korigin.sip.service;

import com.korigin.sip.dto.request.PoemRequest;
import com.korigin.sip.dto.response.CommentResponse;
import com.korigin.sip.dto.response.PoemDetailResponse;
import com.korigin.sip.dto.response.PoemSummaryResponse;
import com.korigin.sip.entity.Poem;
import com.korigin.sip.entity.PoemLike;
import com.korigin.sip.entity.User;
import com.korigin.sip.entity.enums.Category;
import com.korigin.sip.exception.CustomException;
import com.korigin.sip.exception.ErrorCode;
import com.korigin.sip.repository.CommentRepository;
import com.korigin.sip.repository.PoemLikeRepository;
import com.korigin.sip.repository.PoemRepository;
import com.korigin.sip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoemService {

    private final PoemRepository poemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PoemLikeRepository poemLikeRepository;

    @Transactional(readOnly = true)
    public Page<PoemSummaryResponse> getPoems(Pageable pageable) {
        return poemRepository.findAll(pageable).map(PoemSummaryResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PoemSummaryResponse> getPoemsByCategory(Category category, Pageable pageable) {
        return poemRepository.findByCategory(category, pageable).map(PoemSummaryResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PoemSummaryResponse> searchPoems(String keyword, Pageable pageable) {
        return poemRepository.searchByKeyword(keyword, pageable).map(PoemSummaryResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PoemSummaryResponse> getMyPoems(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return poemRepository.findByAuthorId(user.getId(), pageable).map(PoemSummaryResponse::new);
    }

    @Transactional
    public PoemDetailResponse getPoem(Long id, String email) {
        Poem poem = poemRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.POEM_NOT_FOUND));
        poem.incrementViews();

        boolean isLiked = false;
        boolean isAuthor = false;
        if (email != null) {
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                isLiked = poemLikeRepository.existsByUserIdAndPoemId(user.getId(), id);
                isAuthor = poem.getAuthor() != null && poem.getAuthor().getId().equals(user.getId());
            }
        }

        List<CommentResponse> comments = commentRepository.findByPoemIdOrderByCreatedAtAsc(id)
            .stream().map(CommentResponse::new).toList();
        return new PoemDetailResponse(poem, comments, isLiked, isAuthor);
    }

    @Transactional
    public PoemDetailResponse createPoem(String email, PoemRequest request) {
        User author = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Poem poem = Poem.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .category(request.getCategory())
            .anonymous(request.isAnonymous())
            .author(author)
            .build();
        poemRepository.save(poem);
        return new PoemDetailResponse(poem, List.of(), false, true);
    }

    @Transactional
    public PoemDetailResponse updatePoem(String email, Long poemId, PoemRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Poem poem = poemRepository.findById(poemId)
            .orElseThrow(() -> new CustomException(ErrorCode.POEM_NOT_FOUND));
        if (!poem.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        poem.setTitle(request.getTitle());
        poem.setContent(request.getContent());
        poem.setCategory(request.getCategory());
        poem.setAnonymous(request.isAnonymous());

        List<CommentResponse> comments = commentRepository.findByPoemIdOrderByCreatedAtAsc(poemId)
            .stream().map(CommentResponse::new).toList();
        return new PoemDetailResponse(poem, comments, false, true);
    }

    @Transactional
    public void deletePoem(String email, Long poemId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Poem poem = poemRepository.findById(poemId)
            .orElseThrow(() -> new CustomException(ErrorCode.POEM_NOT_FOUND));
        if (!poem.getAuthor().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        poemRepository.delete(poem);
    }

    @Transactional
    public int toggleLike(String email, Long poemId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Poem poem = poemRepository.findById(poemId)
            .orElseThrow(() -> new CustomException(ErrorCode.POEM_NOT_FOUND));

        var existing = poemLikeRepository.findByUserIdAndPoemId(user.getId(), poemId);
        if (existing.isPresent()) {
            poemLikeRepository.delete(existing.get());
        } else {
            poemLikeRepository.save(PoemLike.builder().user(user).poem(poem).build());
        }
        return (int) poemLikeRepository.countByPoemId(poemId);
    }
}
