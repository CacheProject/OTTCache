package com.example.cacheproject.domain.auth.service;

import com.example.cacheproject.common.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.cacheproject.domain.auth.dto.request.LoginRequestDto;
import com.example.cacheproject.domain.auth.dto.response.LoginResponseDto;
import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 로그인
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken(user);

        // 쿠키에 리프레시 토큰 저장
        setRefreshTokenInCookie(response, refreshToken);

        return new LoginResponseDto(
                accessToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getUserRole().name()
        );
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId, HttpServletResponse response) {
        tokenService.revokeRefreshToken(userId);
        removeRefreshTokenCookie(response);
    }

    // 토큰 재발급
    @Transactional
    public LoginResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        User user = tokenService.validateRefreshTokenAndGetUser(refreshToken);

        String newAccessToken = tokenService.createAccessToken(user);
        String newRefreshToken = tokenService.createRefreshToken(user);

        setRefreshTokenInCookie(response, newRefreshToken);

        return new LoginResponseDto(
                newAccessToken,
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getUserRole().name()
        );
    }

    private void setRefreshTokenInCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        response.addCookie(cookie);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie expired = new Cookie("refreshToken", null);
        expired.setHttpOnly(true);
        expired.setPath("/");
        expired.setMaxAge(0);
        response.addCookie(expired);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("refreshToken"))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("리프레시 토큰이 없습니다."));
    }
}