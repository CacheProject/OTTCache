package com.example.cacheproject.domain.auth.service;

import com.example.cacheproject.common.exception.NotFoundException;
import com.example.cacheproject.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import com.example.cacheproject.domain.auth.entity.RefreshToken;
import com.example.cacheproject.domain.auth.enums.TokenStatus;
import com.example.cacheproject.domain.auth.repository.RefreshTokenRepository;
import com.example.cacheproject.common.util.JwtUtil;
import com.example.cacheproject.domain.user.service.UserService;
import com.example.cacheproject.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Transactional
    public String createAccessToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    @Transactional
    public String createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserIdForUpdate(user.getId())
                .orElseGet(RefreshToken::create);

        refreshToken.reIssue();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional
    public void revokeRefreshToken(Long userId) {
        RefreshToken refreshToken = findByUserId(userId);
        refreshToken.updateTokenStatus(TokenStatus.INVALIDATED);
    }

    @Transactional
    public User validateRefreshTokenAndGetUser(String token) {
        RefreshToken refreshToken = findByToken(token);

        if (refreshToken.getTokenStatus() == TokenStatus.INVALIDATED) {
            throw new UnauthorizedException("만료된 토큰입니다.");
        }

        refreshToken.updateTokenStatus(TokenStatus.INVALIDATED);
        return userService.getUserByIdOrThrow(refreshToken.getUserId());
    }

    @Transactional
    private RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new NotFoundException("존재하지 않는 토큰입니다.")
        );
    }

    @Transactional
    private RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("토큰 확인 불가")
        );
    }
}
