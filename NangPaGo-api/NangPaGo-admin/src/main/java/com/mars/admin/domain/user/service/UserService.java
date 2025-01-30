package com.mars.admin.domain.user.service;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mars.common.exception.NPGExceptionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private static final int PAGE_SIZE = 10;
    private final UserRepository userRepository;

    public UserResponseDto getCurrentUser(Long userId) {
        return UserResponseDto.from(userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of));
    }

    public Page<UserDetailResponseDto> getUserList(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return userRepository.findByRoleNotAdmin(pageable).map(UserDetailResponseDto::from);
    }

    @Transactional
    public UserBanResponseDto banUser(Long userId) {
        User user = findUserById(userId);
        user.updateUserStatus(UserStatus.BANNED);
        return UserBanResponseDto.from(user);
    }

    @Transactional
    public UserBanResponseDto unbanUser(Long userId) {
        User user = findUserById(userId);
        user.updateUserStatus(UserStatus.ACTIVE);
        return UserBanResponseDto.from(user);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);
    }
}
