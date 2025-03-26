package org.example.cacheproject.domain.auth.service;

import lombok.RequiredArgsConstructor;
import com.example.plusproject.exception.UnauthorizedException;
import com.example.plusproject.exception.NotFoundException;
import org.example.cacheproject.domain.auth.entity.RefreshToken;
import org.example.cacheproject.domain.auth.enums.TokenStatus;
import org.example.cacheproject.domain.auth.repository.RefreshTokenRepository;
import org.example.cacheproject.config.JwtUtil;
import org.example.cacheproject.domain.user.service.UserService;
import org.example.cacheproject.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public String createAccessToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    public String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken(user.getId());
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public void revokeRefreshToken(Long userId) {
        RefreshToken refreshToken = findByUserId(userId);
        refreshToken.updateTokenStatus(TokenStatus.INVALIDATED);
    }

    public User validateRefreshTokenAndGetUser(String token) {
        RefreshToken refreshToken = findByToken(token);

        if (refreshToken.getTokenStatus() == TokenStatus.INVALIDATED) {
            throw new UnauthorizedException("만료된 토큰입니다.");
        }

        refreshToken.updateTokenStatus(TokenStatus.INVALIDATED);
        return userService.getUserByIdOrThrow(refreshToken.getUserId());
    }

    private RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new NotFoundException("존재하지 않는 토큰입니다.")
        );
    }

    private RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("토큰 확인 불가")
        );
    }
}
