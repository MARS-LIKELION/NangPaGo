package com.mars.admin.domain.user.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.enums.UserListSearchType;
import com.mars.admin.domain.user.enums.UserListSortType;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.model.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getCurrentUser(Long userId) {
        return UserResponseDto.from(userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of));
    }

    public PageResponseDto<UserDetailResponseDto> getUserList(PageRequestVO pageRequestVO, UserListSortType sort,
        List<UserStatus> statuses, List<OAuth2Provider> providers, UserListSearchType searchType, String searchKeyword) {

        String searchTypeKey = getSearchTypeKey(searchType);

        Page<User> users = switch (sort) {
            case NICKNAME_ASC -> userRepository.findByRoleNotAdminWithFiltersOrderByNicknameAsc(
                statuses, providers, searchTypeKey, searchKeyword, pageRequestVO.toPageable());
            case NICKNAME_DESC -> userRepository.findByRoleNotAdminWithFiltersOrderByNicknameDesc(
                statuses, providers, searchTypeKey, searchKeyword, pageRequestVO.toPageable());
            default -> userRepository.findByRoleNotAdminWithFilters(
                statuses, providers, searchTypeKey, searchKeyword,
                PageRequest.of(pageRequestVO.pageNo() - 1, pageRequestVO.pageSize(),
                    Sort.by(sort.getDirection(), sort.getField()))
            );
        };

        return PageResponseDto.of(users.map(UserDetailResponseDto::from));
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

    private String getSearchTypeKey(UserListSearchType userListSearchType) {
        if (userListSearchType != null) {
            return userListSearchType.getKey();
        }
        return null;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);
    }
}
