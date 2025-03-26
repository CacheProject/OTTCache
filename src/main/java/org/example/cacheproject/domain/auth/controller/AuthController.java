package org.example.cacheproject.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cacheproject.common.response.ApiResponse;
import org.example.cacheproject.domain.auth.dto.request.LoginRequestDto;
import org.example.cacheproject.domain.auth.dto.response.LoginResponseDto;
import org.example.cacheproject.domain.auth.service.AuthService;
import org.example.cacheproject.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResponseDto responseDto = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.of("로그인 성공", responseDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUser().getId(), response);
        return ResponseEntity.ok(ApiResponse.of("로그아웃 완료", null));
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDto responseDto = authService.refreshToken(request, response);
        return ResponseEntity.ok(ApiResponse.of("토큰 재발급 성공", responseDto));
    }
}