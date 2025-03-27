package com.example.cacheproject.domain.user.controller;

import com.example.cacheproject.common.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.cacheproject.domain.user.dto.request.UserDeleteRequestDto;
import com.example.cacheproject.domain.user.dto.request.UserSignupRequestDto;
import com.example.cacheproject.domain.user.dto.request.UserUpdateRequestDto;
import com.example.cacheproject.domain.user.dto.response.UserListResponseDto;
import com.example.cacheproject.domain.user.dto.response.UserProfileResponseDto;
import com.example.cacheproject.domain.user.dto.response.UserSignupResponseDto;
import com.example.cacheproject.domain.user.dto.response.UserUpdateResponseDto;
import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.user.service.UserService;
import com.example.cacheproject.security.CustomUserDetails;
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
    public ResponseEntity<Response<UserSignupResponseDto>> signup(@RequestBody @Valid UserSignupRequestDto request) {
        UserSignupResponseDto responseDto = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(responseDto));
    }

    @GetMapping
    public ResponseEntity<Response<List<UserListResponseDto>>> getAllUsers() {
        return ResponseEntity.ok(Response.of(userService.getAllUsers()));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response<UserProfileResponseDto>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(Response.of(userService.getMyProfile(user)));
    }

    @PatchMapping
    public ResponseEntity<Response<UserUpdateResponseDto>> updateUsername(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid UserUpdateRequestDto request) {
        User user = userDetails.getUser();
        String updatedUsername = userService.updateUsername(user, request.getUsername());
        return ResponseEntity.ok(Response.of(new UserUpdateResponseDto(updatedUsername)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid UserDeleteRequestDto request) {
        User user = userDetails.getUser();
        userService.deleteUser(user, request.getPassword());
        return ResponseEntity.ok(Response.of( null));
    }
}
