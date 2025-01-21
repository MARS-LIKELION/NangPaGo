package com.mars.admin.auth.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.admin.auth.vo.UserDetailsImpl;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(NOT_FOUND_USER::of);

        return new UserDetailsImpl(
            new UserResponseDto(user.getEmail(), user.getNickname(), user.getRole(), user.getPassword()));
    }
}
