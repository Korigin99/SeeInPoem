package com.korigin.sip.controller;

import com.korigin.sip.dto.request.CommentRequest;
import com.korigin.sip.dto.request.CommentUpdateRequest;
import com.korigin.sip.dto.response.CommentResponse;
import com.korigin.sip.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/poems/{poemId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long poemId,
        @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.addComment(userDetails.getUsername(), poemId, request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long poemId,
        @PathVariable Long commentId,
        @Valid @RequestBody CommentUpdateRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(userDetails.getUsername(), commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long poemId,
        @PathVariable Long commentId
    ) {
        commentService.deleteComment(userDetails.getUsername(), commentId);
        return ResponseEntity.noContent().build();
    }
}
