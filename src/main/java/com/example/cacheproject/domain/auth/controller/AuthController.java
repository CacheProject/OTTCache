package com.example.cacheproject.domain.auth.controller;

import com.example.cacheproject.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.cacheproject.domain.auth.dto.request.LoginRequestDto;
import com.example.cacheproject.domain.auth.dto.response.LoginResponseDto;
import com.example.cacheproject.domain.auth.service.AuthService;
import com.example.cacheproject.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResponseDto responseDto = authService.login(request, response);
        return ResponseEntity.ok(Response.of(responseDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<Response<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUser().getId(), response);
        return ResponseEntity.ok(Response.of(null));
    }

    @GetMapping("/refresh")
    public ResponseEntity<Response<LoginResponseDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDto responseDto = authService.refreshToken(request, response);
        return ResponseEntity.ok(Response.of(responseDto));
    }
}