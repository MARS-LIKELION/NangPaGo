package com.mars.admin.domain.user.service;

import com.mars.admin.domain.user.dto.UserDto;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.exception.NPGExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private static int PAGESIZE = 10;

    public UserResponseDto getCurrentUser(String email) {
        return UserResponseDto.from(userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of));
    }

    public Page<UserDto> getUserList(int page) {
        Pageable pageable = PageRequest.of(page, PAGESIZE);
        return userRepository.findByUsers(pageable).map(UserDto::from);
    }
}
