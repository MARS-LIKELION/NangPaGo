package com.mars.admin.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.admin.domain.user.sort.UserListSortType;
import com.mars.admin.support.IntegrationTestSupport;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.exception.NPGException;
import com.mars.common.model.user.User;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User createUser(String email, String nickname) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .userStatus(UserStatus.ACTIVE)
            .role("ROLE_USER")
            .build();
    }

    @DisplayName("사용자 정보를 조회할 수 있다.")
    @Test
    void getCurrentUser() {
        // given
        User user = createUser("test@example.com", "nickname");
        userRepository.save(user);

        // when
        UserResponseDto result = userService.getCurrentUser(user.getId());

        // then
        assertThat(result.email()).isEqualTo("test@example.com");
    }

    @DisplayName("존재하지 않는 사용자를 조회하면 예외가 발생한다.")
    @Test
    void getCurrentUser_NotFound() {
        // when & then
        assertThatThrownBy(() -> userService.getCurrentUser(999L))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("사용자 목록을 페이지네이션하여 조회할 수 있다.")
    @Test
    void getUserList() {
        // given
        List<User> users = IntStream.range(0, 15)
            .mapToObj(i -> createUser("test" + i + "@example.com", "nickname"))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        Page<UserDetailResponseDto> result = userService.getUserList(0, UserListSortType.ID_ASC);

        // then
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @DisplayName("사용자 목록을 Id를 기준으로 내림차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByIdDesc() {
        // given
        List<User> users = IntStream.range(0, 15)
                .mapToObj(i -> createUser("test" + i + "@example.com", "nickname"))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        Page<UserDetailResponseDto> firstPage = userService.getUserList(0, UserListSortType.ID_DESC);
        Page<UserDetailResponseDto> secondPage = userService.getUserList(1, UserListSortType.ID_DESC);

        // then
        assertThat(firstPage.getContent().get(0).id()).isGreaterThan(secondPage.getContent().get(4).id());
    }

    @DisplayName("사용자 목록을 Id를 기준으로 오름차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByIdAsc() {
        // given
        List<User> users = IntStream.range(0, 15)
                .mapToObj(i -> createUser("test" + i + "@example.com", "nickname"))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        Page<UserDetailResponseDto> firstPage = userService.getUserList(0, UserListSortType.ID_ASC);
        Page<UserDetailResponseDto> secondPage = userService.getUserList(1, UserListSortType.ID_ASC);

        // then
        assertThat(firstPage.getContent().get(0).id()).isLessThan(secondPage.getContent().get(4).id());
    }

    @DisplayName("사용자 목록을 닉네임을 기준으로 내림차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByNicknameDesc() {
        // given
        List<User> users = IntStream.range(0, 15)
                .mapToObj(i -> createUser("test" + i + "@example.com"
                        , "nickname" + i))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        Page<UserDetailResponseDto> firstPage = userService
                .getUserList(0, UserListSortType.NICKNAME_DESC);
        Page<UserDetailResponseDto> secondPage = userService
                .getUserList(1, UserListSortType.NICKNAME_DESC);

        // then
        assertThat(firstPage.getContent().get(0).nickname()).isEqualTo("nickname14");
        assertThat(secondPage.getContent().get(4).nickname()).isEqualTo("nickname0");
    }

    @DisplayName("사용자 목록을 닉네임을 기준으로 오름차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByNicknameAsc() {
        // given
        List<User> users = IntStream.range(0, 15)
                .mapToObj(i -> createUser("test" + i + "@example.com"
                        , "nickname" + i))
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        Page<UserDetailResponseDto> firstPage = userService
                .getUserList(0, UserListSortType.NICKNAME_ASC);
        Page<UserDetailResponseDto> secondPage = userService
                .getUserList(1, UserListSortType.NICKNAME_ASC);

        // then
        assertThat(firstPage.getContent().get(0).nickname()).isEqualTo("nickname0");
        assertThat(secondPage.getContent().get(4).nickname()).isEqualTo("nickname14");
    }

    @DisplayName("사용자를 차단할 수 있다.")
    @Test
    void banUser() {
        // given
        User user = createUser("test@example.com", "nickname");
        userRepository.save(user);

        // when
        UserBanResponseDto result = userService.banUser(user.getId());

        // then
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.BANNED);
    }

    @DisplayName("사용자의 차단을 해제할 수 있다.")
    @Test
    void unbanUser() {
        // given
        User user = createUser("test@example.com", "nickname");
        user.updateUserStatus(UserStatus.BANNED);
        userRepository.save(user);

        // when
        UserBanResponseDto result = userService.unbanUser(user.getId());

        // then
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("존재하지 않는 사용자를 차단하려고 하면 예외가 발생한다.")
    @Test
    void banUser_NotFound() {
        // when & then
        assertThatThrownBy(() -> userService.banUser(999L))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다");
    }
}
