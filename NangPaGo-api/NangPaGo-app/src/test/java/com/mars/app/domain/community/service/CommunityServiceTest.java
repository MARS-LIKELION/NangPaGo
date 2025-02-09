package com.mars.app.domain.community.service;

import com.mars.app.domain.community.repository.CommunityCommentRepository;
import com.mars.app.domain.community.dto.CommunityRequestDto;
import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.model.community.Community;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.mars.common.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class CommunityServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityLikeRepository communityLikeRepository;

    @Autowired
    private CommunityCommentRepository communityCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    private User createUser(String nickname) {
        return User.builder()
            .nickname(nickname)
            .build();
    }

    @DisplayName("사용자는 게시글을 생성할 수 있다.")
    @Test
    void createCommunityTest() {
        // given
        User user = createUser("kimdonghwan");
        userRepository.save(user);

        CommunityRequestDto requestDto = new CommunityRequestDto(
            "테스트제목",
            "테스트내용",
            null,
            true
        );

        // when
        CommunityResponseDto result = communityService.createCommunity(requestDto, null, user.getId());

        // then
        assertThat(result.title()).isEqualTo("테스트제목");
        assertThat(result.content()).isEqualTo("테스트내용");
        assertThat(result.imageUrl()).isNotBlank();
        assertThat(result.nickname()).isEqualTo("kimdonghwan");
        assertThat(result.isPublic()).isTrue();
        assertThat(result.likeCount()).isZero();
        assertThat(result.commentCount()).isZero();
        assertThat(result.isOwnedByUser()).isTrue();
    }

    @DisplayName("사용자는 공개상태인 게시글을 확인할 수 있다.")
    @Test
    void getCommunityById_public() {
        // given
        User user = createUser("kimdonghwan");
        userRepository.save(user);

        // of(user, title, content, imageUrl, isPublic)
        Community community = Community.of(user, "공개제목", "공개내용", null, true);
        communityRepository.save(community);

        // when
        CommunityResponseDto result = communityService.getCommunityById(community.getId(), 9999L);

        // then
        assertThat(result.title()).isEqualTo("공개제목");
        assertThat(result.content()).isEqualTo("공개내용");
        assertThat(result.isOwnedByUser()).isFalse(); //작성자 아님
        assertThat(result.isPublic()).isTrue(); //공개글
    }

    @DisplayName("공개상태인 게시글과 내가 작성한 게시글을 리스트로 조회할 수 있다.")
    @Test
    void getPagesByCommunity() {
        // given
        User user = createUser("kimdonghwan");
        User userOther = createUser("kimdonghwan");
        userRepository.saveAll(List.of(user, userOther));

        Community communityPublic = Community.of(user, "공개제목", "공개내용", null, true);
        Community communityPrivate = Community.of(user, "비공개제목", "비공개내용", null, false);
        Community communityOtherAuthorPublic = Community.of(userOther, "공개제목 다른 유저", "공개내용 다른 유저", null, true);
        Community communityOtherAuthorPrivate = Community.of(userOther, "비공개제목 다른 유저", "비공개내용 다른 유저", null, false);
        communityRepository.saveAll(List.of(
            communityPublic,
            communityPrivate,
            communityOtherAuthorPublic,
            communityOtherAuthorPrivate
        ));


        PageRequestVO pageRequestVO = new PageRequestVO(1, 12);
        // when
        PageResponseDto<CommunityResponseDto> communityResponseDtoPageDto = communityService.pagesByCommunity(
            user.getId(), pageRequestVO);

        // then
        assertThat(communityResponseDtoPageDto)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 3L);
        assertThat(communityResponseDtoPageDto.getContent())
            .extracting(CommunityResponseDto::id)
            .doesNotContain(communityOtherAuthorPrivate.getId());
    }

    @DisplayName("비공개 글을 작성자가 아닌 다른 사용자가 조회하면 예외가 발생한다.")
    @Test
    void getCommunityById_private() {
        // given
        User author = createUser("kimdonghwan");
        userRepository.save(author);

        Community privateCommunity = Community.of(author, "비공개제목", "비공개내용", null, false);
        communityRepository.save(privateCommunity);

        // when , then
        Exception ex = assertThrows(
            RuntimeException.class,
            () -> communityService.getCommunityById(privateCommunity.getId(), 9999L)
        );
        assertThat(ex.getMessage()).contains("게시물을 수정/삭제할 권한이 없습니다.");
    }

    @DisplayName("사용자는 게시글을 수정할 수 있다.")
    @Test
    void updateCommunityTest() {
        // given
        User user = createUser("kimdonghwan");
        userRepository.save(user);

        Community community = Community.of(user, "원제목", "원내용", "sample.png", true);
        communityRepository.save(community);

        CommunityRequestDto updateDto = new CommunityRequestDto(
            "새제목",
            "새내용",
            "updatedImage.png",
            false
        );

        // when
        CommunityResponseDto result = communityService.updateCommunity(
            community.getId(),
            updateDto,
            null,
            user.getId()
        );

        // then
        assertThat(result.title()).isEqualTo("새제목");
        assertThat(result.content()).isEqualTo("새내용");
        assertThat(result.imageUrl()).isEqualTo("sample.png");
        assertThat(result.isPublic()).isFalse();
        assertThat(result.isOwnedByUser()).isTrue();
    }

    @DisplayName("사용자는 게시글을 삭제할 수 있다.")
    @Test
    void deleteCommunityTest() {
        // given
        User user = createUser("kimdonghwan");
        userRepository.save(user);

        Community community = Community.of(user, "삭제테스트", "삭제내용", null, true);
        communityRepository.save(community);

        // when
        communityService.deleteCommunity(community.getId(), user.getId());

        // then
        boolean exists = communityRepository.existsById(community.getId());
        assertThat(exists).isFalse();
    }

    @DisplayName("게시글 수정을 위한 현재 게시글 상세 정보를 조회할 수 있다.")
    @Test
    void getPostForEditTest() {
        // given
        User user = createUser("kimdonghwan");
        userRepository.save(user);

        Community community = Community.of(user, "수정 상세 테스트", "수정 상세 내용", null, true);
        communityRepository.save(community);

        // when
        CommunityResponseDto postForEdit = communityService.getPostForEdit(community.getId(), user.getId());

        // then
        assertThat(postForEdit.id()).isEqualTo(community.getId());
        assertThat(postForEdit.title()).isEqualTo("수정 상세 테스트");
        assertThat(postForEdit.content()).isEqualTo("수정 상세 내용");
        assertThat(postForEdit.isPublic()).isTrue();
    }
}
