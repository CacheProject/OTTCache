package com.example.cacheproject.domain.user.service;

import com.example.cacheproject.domain.store.repository.StoreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.cacheproject.domain.user.dto.request.UserSignupRequestDto;
import com.example.cacheproject.domain.user.dto.response.UserSignupResponseDto;
import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.user.repository.UserRepository;
import com.example.cacheproject.common.exception.BadRequestException;
import com.example.cacheproject.common.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입_USER_정상() {
        // Given
        UserSignupRequestDto request = new UserSignupRequestDto("test@sample.com", "testUser", "password1234", "password1234", "USER", null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        UserSignupResponseDto result = userService.signup(request);

        // Then
        assertEquals("회원가입 완료", result.getMessage());
        verify(userRepository).save(any(User.class));
        verify(storeRepository, never()).save(any());
    }

    @Test
    void 회원가입_OWNER_가게정보없으면_예외() {
        // Given
        UserSignupRequestDto request = new UserSignupRequestDto("test@sample.com", "testOwner", "password1234", "password1234", "OWNER", null);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Then
        assertThrows(BadRequestException.class, () -> userService.signup(request));
    }

    @Test
    void 회원_탈퇴_정상처리() {
        // Given
        User user = new User("testUser", "test@sample.com", "encodedPassword", "USER");
        when(passwordEncoder.matches("password1234", "encodedPassword")).thenReturn(true);

        // When
        userService.deleteUser(user, "password1234");

        // Then
        verify(userRepository).delete(user);
        verify(storeRepository).deleteByUserId(user.getId());
    }

    @Test
    void 회원_탈퇴_비번_불일치로_예외() {
        // Given
        User user = new User("testUser", "test@sample.com", "encodedPassword", "USER");
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // Then
        assertThrows(UnauthorizedException.class, () -> userService.deleteUser(user, "wrongPassword"));
    }
}