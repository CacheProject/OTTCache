package com.example.cacheproject.domain.user.service;

import com.example.cacheproject.common.exception.BadRequestException;
import com.example.cacheproject.common.exception.NotFoundException;
import com.example.cacheproject.common.exception.UnauthorizedException;
import com.example.cacheproject.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.cacheproject.domain.user.dto.request.UserSignupRequestDto;
import com.example.cacheproject.domain.user.dto.response.UserListResponseDto;
import com.example.cacheproject.domain.user.dto.response.UserProfileResponseDto;
import com.example.cacheproject.domain.user.dto.response.UserSignupResponseDto;
import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.store.entity.Store;
import com.example.cacheproject.domain.user.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호 검증
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BadRequestException("비밀번호를 확인해주세요");
        }

        // 비번 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getEmail(), request.getUsername(), encodedPassword, request.getRole());
        userRepository.save(user);

        Long storeId = null;

        // 관리자나 사장일경우 가게 정보 등록
        if (user.getUserRole().canHaveStore()) {
            if (request.getStore() == null) {
                throw new BadRequestException("가게 정보가 필요합니다.");
            }
            Store store = new Store(request.getStore(), user.getId());
            storeRepository.save(store);
            storeId = store.getId();
        }

        return new UserSignupResponseDto("회원가입 완료", user.getId(), storeId);
    }

    @Cacheable(value = "userListCache", key = "'all'")
    @Transactional(readOnly = true)
    public List<UserListResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserListResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getMyProfile(User user) {
        Store store = storeRepository.findByUserId(user.getId()).orElse(null);
        return UserProfileResponseDto.from(user, store);
    }

    @Transactional
    public String updateUsername(User user, String newUsername) {
        user.updateUsername(newUsername);
        return userRepository.save(user).getUsername();
    }

    @Transactional
    public void deleteUser(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        storeRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    @Transactional
    public User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("해당 유저가 존재하지 않습니다.")
        );
    }
}
