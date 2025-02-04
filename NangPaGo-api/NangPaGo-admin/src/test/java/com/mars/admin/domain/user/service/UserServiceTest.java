package com.mars.admin.domain.user.service;

import static com.mars.admin.domain.user.sort.UserListSortType.ID_ASC;
import static com.mars.admin.domain.user.sort.UserListSortType.ID_DESC;
import static com.mars.admin.domain.user.sort.UserListSortType.NICKNAME_ASC;
import static com.mars.admin.domain.user.sort.UserListSortType.NICKNAME_DESC;
import static com.mars.common.enums.oauth.OAuth2Provider.GOOGLE;
import static com.mars.common.enums.oauth.OAuth2Provider.KAKAO;
import static com.mars.common.enums.oauth.OAuth2Provider.NAVER;
import static com.mars.common.enums.user.UserStatus.ACTIVE;
import static com.mars.common.enums.user.UserStatus.BANNED;
import static com.mars.common.enums.user.UserStatus.WITHDRAWN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.admin.support.IntegrationTestSupport;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.exception.NPGException;
import com.mars.common.model.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User createUser(String email, String nickname, OAuth2Provider oAuth2Provider) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .oauth2Provider(oAuth2Provider)
            .userStatus(ACTIVE)
            .role("ROLE_USER")
            .build();
    }

    @DisplayName("사용자 정보를 조회할 수 있다.")
    @Test
    void getCurrentUser() {
        // given
        User user = createUser("test@example.com", "nickname", GOOGLE);
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
            .mapToObj(i -> createUser("test" + i + "@example.com", "nickname", GOOGLE))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 10);
        // when
        PageResponseDto<UserDetailResponseDto> result = userService.getUserList(pageRequestVO, ID_ASC, null, null);

        // then
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getTotalItems()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @DisplayName("사용자 목록을 Id를 기준으로 내림차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByIdDesc() {
        // given
        List<User> users = IntStream.range(0, 15)
            .mapToObj(i -> createUser("test" + i + "@example.com", "nickname", GOOGLE))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService.getUserList(PageRequestVO.of(1, 10), ID_DESC,
            null, null);
        PageResponseDto<UserDetailResponseDto> secondPage = userService.getUserList(PageRequestVO.of(2, 10), ID_DESC,
            null, null);

        // then
        assertThat(firstPage.getContent().get(0).id()).isGreaterThan(secondPage.getContent().get(4).id());
    }

    @DisplayName("사용자 목록을 Id를 기준으로 오름차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByIdAsc() {
        // given
        List<User> users = IntStream.range(0, 15)
            .mapToObj(i -> createUser("test" + i + "@example.com", "nickname", GOOGLE))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService.getUserList(PageRequestVO.of(1, 10), ID_ASC,
            null, null);
        PageResponseDto<UserDetailResponseDto> secondPage = userService.getUserList(PageRequestVO.of(2, 10), ID_ASC,
            null, null);

        // then
        System.out.println(firstPage.getContent().get(0).id());
        System.out.println(secondPage.getContent().get(4).id());
        assertThat(firstPage.getContent().get(0).id()).isLessThan(secondPage.getContent().get(4).id());
    }

    @DisplayName("사용자 목록을 닉네임을 기준으로 내림차순 정렬할 수 있다.")
    @Test
    void getUserListSortedByNicknameDesc() {
        // given
        List<User> users = IntStream.range(0, 15)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, GOOGLE))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_DESC, null, null);
        PageResponseDto<UserDetailResponseDto> secondPage = userService
            .getUserList(PageRequestVO.of(2, 10), NICKNAME_DESC, null, null);

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
                , "nickname" + i, GOOGLE))
            .collect(Collectors.toList());
        userRepository.saveAll(users);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_ASC, null, null);
        PageResponseDto<UserDetailResponseDto> secondPage = userService
            .getUserList(PageRequestVO.of(2, 10), NICKNAME_ASC, null, null);

        // then
        assertThat(firstPage.getContent().get(0).nickname()).isEqualTo("nickname0");
        assertThat(secondPage.getContent().get(4).nickname()).isEqualTo("nickname14");
    }

    @DisplayName("사용자 목록에서 밴된 유저만 조회할 수 있다.")
    @Test
    void getUserListFilterUserStatusBan() {
        // given
        List<User> users = IntStream.range(0, 10)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, GOOGLE))
            .collect(Collectors.toList());

        users.get(7).updateUserStatus(BANNED);

        userRepository.saveAll(users);

        List<UserStatus> userStatuses = List.of(BANNED);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_ASC, userStatuses, null);

        // then
        assertThat(firstPage.getContent().get(0).nickname()).isEqualTo("nickname7");
        assertThat(firstPage.getTotalItems()).isEqualTo(1);
    }

    @DisplayName("사용자 목록에서 네이버 유저만 조회할 수 있다.")
    @Test
    void getUserListFilterOAuth2ProviderNaver() {
        // given
        List<User> users = IntStream.range(0, 10)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, GOOGLE))
            .collect(Collectors.toList());

        users.add(createUser("test10@example.com", "nickname10", NAVER));
        users.add(createUser("test11@example.com", "nickname11", NAVER));
        users.add(createUser("test12@example.com", "nickname12", GOOGLE));
        users.add(createUser("test13@example.com", "nickname13", NAVER));

        userRepository.saveAll(users);

        List<OAuth2Provider> oAuth2Providers = List.of(NAVER);
        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_ASC, null, oAuth2Providers);

        // then
        assertThat(firstPage.getContent().get(0).nickname()).isEqualTo("nickname10");
        assertThat(firstPage.getTotalItems()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("사용자 목록에서 카카오이면서 탈퇴한 유저를 조회할 수 있다.")
    @Test
    void getUserListFilterOAuth2ProviderKakaoAndUserStatusWithdrawn() {
        // given
        List<User> users = IntStream.range(0, 10)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, KAKAO))
            .collect(Collectors.toList());

        users.add(createUser("test10@example.com", "nickname10", NAVER));
        users.add(createUser("test11@example.com", "nickname11", NAVER));
        users.add(createUser("test12@example.com", "nickname12", GOOGLE));
        users.add(createUser("test13@example.com", "nickname13", NAVER));

        users.get(1).updateUserStatus(WITHDRAWN);
        users.get(3).updateUserStatus(WITHDRAWN);
        users.get(7).updateUserStatus(WITHDRAWN);

        userRepository.saveAll(users);

        List<UserStatus> userStatuses = List.of(WITHDRAWN);
        List<OAuth2Provider> oAuth2Providers = List.of(KAKAO);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_ASC, userStatuses, oAuth2Providers);

        // then
        assertAll("닉네임 검증",
            () -> assertEquals("nickname1", firstPage.getContent().get(0).nickname()),
            () -> assertEquals("nickname3", firstPage.getContent().get(1).nickname()),
            () -> assertEquals("nickname7", firstPage.getContent().get(2).nickname())
        );
        assertThat(firstPage.getTotalItems()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("사용자 목록에서 카카오이면서 탈퇴한 유저를 닉네임 기준 내림차순으로 조회할 수 있다.")
    @Test
    void getUserListSortNicknameDescAndFilterOAuth2ProviderKakaoAndUserStatusWithdrawn() {
        // given
        List<User> users = IntStream.range(0, 10)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, KAKAO))
            .collect(Collectors.toList());

        users.add(createUser("test10@example.com", "nickname10", NAVER));
        users.add(createUser("test11@example.com", "nickname11", NAVER));
        users.add(createUser("test12@example.com", "nickname12", GOOGLE));
        users.add(createUser("test13@example.com", "nickname13", NAVER));

        users.get(1).updateUserStatus(WITHDRAWN);
        users.get(3).updateUserStatus(WITHDRAWN);
        users.get(7).updateUserStatus(WITHDRAWN);

        userRepository.saveAll(users);

        List<UserStatus> userStatuses = List.of(WITHDRAWN);
        List<OAuth2Provider> oAuth2Providers = List.of(KAKAO);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), NICKNAME_DESC, userStatuses, oAuth2Providers);

        // then
        assertAll("닉네임 검증",
            () -> assertEquals("nickname7", firstPage.getContent().get(0).nickname()),
            () -> assertEquals("nickname3", firstPage.getContent().get(1).nickname()),
            () -> assertEquals("nickname1", firstPage.getContent().get(2).nickname())
        );
        assertThat(firstPage.getTotalItems()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("사용자 목록에서 구글이면서 정상적인 유저를 생성일자 기준 내림차순으로 조회할 수 있다.")
    @Test
    void getUserListSortIdDescAndFilterOAuth2ProviderGoogleAndUserStatusActive() {
        // given
        List<User> users = IntStream.range(0, 12)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, GOOGLE))
            .collect(Collectors.toList());

        users.add(createUser("test12@example.com", "nickname12", GOOGLE));
        users.add(createUser("test13@example.com", "nickname13", NAVER));
        users.add(createUser("test14@example.com", "nickname14", GOOGLE));
        users.add(createUser("test15@example.com", "nickname15", NAVER));

        users.get(3).updateUserStatus(WITHDRAWN);
        users.get(7).updateUserStatus(WITHDRAWN);
        users.get(10).updateUserStatus(WITHDRAWN);

        userRepository.saveAll(users);

        List<UserStatus> userStatuses = List.of(ACTIVE);
        List<OAuth2Provider> oAuth2Providers = List.of(GOOGLE);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), ID_DESC, userStatuses, oAuth2Providers);

        // then
        assertAll("닉네임 검증",
            () -> assertEquals("nickname14", firstPage.getContent().get(0).nickname()),
            () -> assertEquals("nickname11", firstPage.getContent().get(2).nickname())
        );
        assertThat(firstPage.getTotalItems()).isEqualTo(11);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
    }

    @DisplayName("사용자 목록에서 구글, 네이버면서 탈퇴,밴 유저를 생성일자 기준 내림차순으로 조회할 수 있다.")
    @Test
    void getUserListSortIdDescAndFilterOAuth2ProvidersAndUserStatuses() {
        // given
        List<User> users = new ArrayList<>(
            IntStream.range(0, 4)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, GOOGLE))
            .toList());
        users.addAll(IntStream.range(4, 8)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, KAKAO))
            .toList());
        users.addAll(IntStream.range(8, 12)
            .mapToObj(i -> createUser("test" + i + "@example.com"
                , "nickname" + i, NAVER))
            .toList());

        users.get(1).updateUserStatus(WITHDRAWN);
        users.get(2).updateUserStatus(BANNED);
        users.get(5).updateUserStatus(BANNED);
        users.get(7).updateUserStatus(WITHDRAWN);
        users.get(9).updateUserStatus(WITHDRAWN);
        users.get(10).updateUserStatus(WITHDRAWN);

        userRepository.saveAll(users);

        List<UserStatus> userStatuses = List.of(BANNED, WITHDRAWN);
        List<OAuth2Provider> oAuth2Providers = List.of(GOOGLE, NAVER);

        // when
        PageResponseDto<UserDetailResponseDto> firstPage = userService
            .getUserList(PageRequestVO.of(1, 10), ID_DESC, userStatuses, oAuth2Providers);

        // then
        assertAll("닉네임 검증",
            () -> assertEquals("nickname10", firstPage.getContent().get(0).nickname()),
            () -> assertEquals("nickname2", firstPage.getContent().get(2).nickname())
        );
        assertThat(firstPage.getTotalItems()).isEqualTo(4);
        assertEquals(WITHDRAWN, firstPage.getContent().get(3).userStatus());
    }

    @DisplayName("사용자를 차단할 수 있다.")
    @Test
    void banUser() {
        // given
        User user = createUser("test@example.com", "nickname", GOOGLE);
        userRepository.save(user);

        // when
        UserBanResponseDto result = userService.banUser(user.getId());

        // then
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(BANNED);
    }

    @DisplayName("사용자의 차단을 해제할 수 있다.")
    @Test
    void unbanUser() {
        // given
        User user = createUser("test@example.com", "nickname", GOOGLE);
        user.updateUserStatus(BANNED);
        userRepository.save(user);

        // when
        UserBanResponseDto result = userService.unbanUser(user.getId());

        // then
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(ACTIVE);
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
