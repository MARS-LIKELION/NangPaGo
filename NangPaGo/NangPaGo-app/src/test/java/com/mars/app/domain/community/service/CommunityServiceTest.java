package com.mars.app.domain.community.service;

import com.mars.app.domain.comment.community.repository.CommunityCommentRepository;
import com.mars.app.domain.community.dto.CommunityRequestDto;
import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.model.community.Community;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.mars.common.model.user.User;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @AfterEach
    void tearDown() {
        communityLikeRepository.deleteAllInBatch();
        communityCommentRepository.deleteAllInBatch();
        communityRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }

    @DisplayName("사용자는 게시글을 생성할 수 있다.")
    @Test
    void createCommunityTest() {
        // given
        User user = createUser("author@example.com");
        userRepository.save(user);

        CommunityRequestDto requestDto = new CommunityRequestDto(
            "테스트제목",
            "테스트내용",
            null,
            true
        );

        // when
        CommunityResponseDto result = communityService.createCommunity(requestDto, null, user.getEmail());

        // then
        assertThat(result.title()).isEqualTo("테스트제목");
        assertThat(result.content()).isEqualTo("테스트내용");
        assertThat(result.imageUrl()).isNotBlank();
        assertThat(result.email()).isEqualTo("aut***@example.com");
        assertThat(result.isPublic()).isTrue();
        assertThat(result.likeCount()).isZero();
        assertThat(result.commentCount()).isZero();
        assertThat(result.isOwnedByUser()).isTrue();
    }

    @DisplayName("사용자는 공개상태인 게시글을 확인할 수 있다.")
    @Test
    void getCommunityById_public() {
        // given
        User user = createUser("author@example.com");
        userRepository.save(user);

        // of(user, title, content, imageUrl, isPublic)
        Community community = Community.of(user, "공개제목", "공개내용", null, true);
        communityRepository.save(community);

        // when
        CommunityResponseDto result = communityService.getCommunityById(community.getId(), "other@example.com");

        // then
        assertThat(result.title()).isEqualTo("공개제목");
        assertThat(result.content()).isEqualTo("공개내용");
        assertThat(result.isOwnedByUser()).isFalse(); //작성자 아님
        assertThat(result.isPublic()).isTrue(); //공개글
    }

    @DisplayName("비공개 글을 작성자가 아닌 다른 사용자가 조회하면 예외가 발생한다.")
    @Test
    void getCommunityById_private() {
        // given
        User author = createUser("author@example.com");
        userRepository.save(author);

        Community privateCommunity = Community.of(author, "비공개제목", "비공개내용", null, false);
        communityRepository.save(privateCommunity);

        // when , then
        Exception ex = assertThrows(
            RuntimeException.class,
            () -> communityService.getCommunityById(privateCommunity.getId(), "other@example.com")
        );
        assertThat(ex.getMessage()).contains("게시물에 접근 권한이 없습니다.");
    }

    @DisplayName("사용자는 게시글을 수정할 수 있다.")
    @Test
    void updateCommunityTest() {
        // given
        User user = createUser("author@example.com");
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
            user.getEmail()
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
        User user = createUser("author@example.com");
        userRepository.save(user);

        Community community = Community.of(user, "삭제테스트", "삭제내용", null, true);
        communityRepository.save(community);

        // when
        communityService.deleteCommunity(community.getId(), user.getEmail());

        // then
        boolean exists = communityRepository.existsById(community.getId());
        assertThat(exists).isFalse();
    }
}
