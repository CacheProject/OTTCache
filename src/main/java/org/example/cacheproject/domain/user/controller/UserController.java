package org.example.cacheproject.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cacheproject.common.response.ApiResponse;
import org.example.cacheproject.domain.user.dto.request.UserDeleteRequestDto;
import org.example.cacheproject.domain.user.dto.request.UserSignupRequestDto;
import org.example.cacheproject.domain.user.dto.request.UserUpdateRequestDto;
import org.example.cacheproject.domain.user.dto.response.UserListResponseDto;
import org.example.cacheproject.domain.user.dto.response.UserProfileResponseDto;
import org.example.cacheproject.domain.user.dto.response.UserSignupResponseDto;
import org.example.cacheproject.domain.user.dto.response.UserUpdateResponseDto;
import org.example.cacheproject.domain.user.entity.User;
import org.example.cacheproject.domain.user.service.UserService;
import org.example.cacheproject.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@RequestBody @Valid UserSignupRequestDto request) {
        UserSignupResponseDto responseDto = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("회원가입 완료", responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserListResponseDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.of("전체 유저 조회", userService.getAllUsers()));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(ApiResponse.of("내 정보 조회", userService.getMyProfile(user)));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUsername(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid UserUpdateRequestDto request) {
        User user = userDetails.getUser();
        String updatedUsername = userService.updateUsername(user, request.getUsername());
        return ResponseEntity.ok(ApiResponse.of("유저 정보 수정 완료", new UserUpdateResponseDto(updatedUsername)));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid UserDeleteRequestDto request) {
        User user = userDetails.getUser();
        userService.deleteUser(user, request.getPassword());
        return ResponseEntity.ok(ApiResponse.of("회원 탈퇴 완료", null));
    }
}
