package org.example.cacheproject.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.cacheproject.common.exception.BadRequestException;
import org.example.cacheproject.common.exception.NotFoundException;
import org.example.cacheproject.common.exception.UnauthorizedException;
import org.example.cacheproject.config.PasswordEncoder;
import org.example.cacheproject.domain.user.dto.request.UserSignupRequestDto;
import org.example.cacheproject.domain.user.dto.response.UserListResponseDto;
import org.example.cacheproject.domain.user.dto.response.UserProfileResponseDto;
import org.example.cacheproject.domain.user.dto.response.UserSignupResponseDto;
import org.example.cacheproject.domain.user.entity.User;
import org.example.cacheproject.domain.user.enums.UserRole;
import org.example.cacheproject.domain.user.repository.UserRepository;
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
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BadRequestException("비밀번호를 확인해주세요");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), encodedPassword, request.getRole());
        userRepository.save(user);

        Long storeId = null;

        if (user.getUserRole() == UserRole.ADMIN) {
            if (request.getStore() == null) {
                throw new BadRequestException("관리자는 store 정보를 포함해야 합니다.");
            }

            Store store = new Store(request.getStore(), user);
            storeRepository.save(store);
            storeId = store.getId();
        }

        return new UserSignupResponseDto("회원가입 완료", user.getId(), storeId);
    }

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
