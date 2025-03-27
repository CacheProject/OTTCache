package com.example.cacheproject.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.cacheproject.domain.auth.dto.request.LoginRequestDto;
import com.example.cacheproject.domain.auth.dto.response.LoginResponseDto;
import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.user.repository.UserRepository;
import com.example.cacheproject.common.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인_성공() {
        // Given
        LoginRequestDto loginDto = new LoginRequestDto("test@email.com", "password1234");
        User user = new User("user", "test@email.com", "encodedPassword", "USER");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password1234", "encodedPassword")).thenReturn(true);
        when(tokenService.createAccessToken(user)).thenReturn("access-token");
        when(tokenService.createRefreshToken(user)).thenReturn("refresh-token");

        // When
        LoginResponseDto result= authService.login(loginDto, response);

        // Then
        assertEquals("access-token", result.getAccessToken());
        assertEquals(user.getUsername(), result.getName());
    }

    @Test
    void 로그인_실패_이메일없음() {
        when(userRepository.findByEmail("none")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequestDto("none", "password1234"), response));
    }

    @Test
    void 로그인_실패_비밀번호틀림() {
        User user = new User("name", "test@sample.com", "encodedPassword", "USER");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequestDto("test@sample.com", "wrong"), response));
    }

    @Test
    void 리프레시_토큰_재발급() {
        // Given
        Cookie cookie = new Cookie("refreshToken", "token1");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        User user = new User("name", "test@sample.com", "password", "USER");

        when(tokenService.validateRefreshTokenAndGetUser("token1")).thenReturn(user);
        when(tokenService.createAccessToken(user)).thenReturn("new-access");
        when(tokenService.createRefreshToken(user)).thenReturn("new-refresh");

        // When
        LoginResponseDto result = authService.refreshToken(request, response);

        // Then
        assertEquals("new-access", result.getAccessToken());
    }

    @Test
    void 로그아웃_성공() {
        authService.logout(1L, response);
        verify(tokenService).revokeRefreshToken(1L);
    }
}