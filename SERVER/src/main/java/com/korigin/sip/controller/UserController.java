package com.korigin.sip.controller;

import com.korigin.sip.dto.request.NicknameUpdateRequest;
import com.korigin.sip.dto.response.UserProfileResponse;
import com.korigin.sip.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/me/nickname")
    public ResponseEntity<Void> updateNickname(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody NicknameUpdateRequest request
    ) {
        userService.updateNickname(userDetails.getUsername(), request.getNickname());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
