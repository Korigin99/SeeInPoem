package com.korigin.sip.controller;

import com.korigin.sip.dto.request.PoemRequest;
import com.korigin.sip.dto.response.PoemDetailResponse;
import com.korigin.sip.dto.response.PoemSummaryResponse;
import com.korigin.sip.entity.enums.Category;
import com.korigin.sip.service.PoemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/poems")
@RequiredArgsConstructor
public class PoemController {

    private final PoemService poemService;

    @GetMapping
    public ResponseEntity<Page<PoemSummaryResponse>> getPoems(
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(poemService.searchPoems(keyword, pageable));
        }
        if (category != null) {
            return ResponseEntity.ok(poemService.getPoemsByCategory(category, pageable));
        }
        return ResponseEntity.ok(poemService.getPoems(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<PoemSummaryResponse>> getMyPoems(
        @AuthenticationPrincipal UserDetails userDetails,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(poemService.getMyPoems(userDetails.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoemDetailResponse> getPoem(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(poemService.getPoem(id, email));
    }

    @PostMapping
    public ResponseEntity<PoemDetailResponse> createPoem(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody PoemRequest request
    ) {
        return ResponseEntity.ok(poemService.createPoem(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PoemDetailResponse> updatePoem(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id,
        @Valid @RequestBody PoemRequest request
    ) {
        return ResponseEntity.ok(poemService.updatePoem(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoem(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id
    ) {
        poemService.deletePoem(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Integer> toggleLike(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(poemService.toggleLike(userDetails.getUsername(), id));
    }
}
