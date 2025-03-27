package com.example.cacheproject.security;

import com.example.cacheproject.domain.user.entity.User;
import com.example.cacheproject.domain.user.repository.UserRepository;
import com.example.cacheproject.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UnauthorizedException("사용자를 찾을 수 없습니다.")
        );
        return new CustomUserDetails(user);
    }
}
