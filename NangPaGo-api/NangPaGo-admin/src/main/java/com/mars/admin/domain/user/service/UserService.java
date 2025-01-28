package com.mars.admin.domain.user.service;

import com.mars.admin.domain.user.dto.UserDto;
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

import static com.mars.common.exception.NPGExceptionType.CONFLICT_USER_STATUS;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private static int PAGESIZE = 10;
    private final UserRepository userRepository;

    public UserResponseDto getCurrentUser(String email) {
        return UserResponseDto.from(userRepository.findByEmail(email)
                .orElseThrow(NOT_FOUND_USER::of));
    }

    public Page<UserDto> getUserList(int page) {
        Pageable pageable = PageRequest.of(page, PAGESIZE);
        return userRepository.findByUsers(pageable).map(UserDto::from);
    }

    @Transactional
    public void ban(long userId) {
        User user = findUserById(userId);
        checkUserStatus(user, UserStatus.BANNED);
        user.ban();
    }

    @Transactional
    public void unBan(long userId) {
        User user = findUserById(userId);
        checkUserStatus(user, UserStatus.ACTIVE);
        user.unban();
    }


    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);
    }

    private void checkUserStatus(User user, UserStatus userStatus){
        if(user.getUserStatus() == userStatus){
            throw CONFLICT_USER_STATUS.of();
        }
    }
}
