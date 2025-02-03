package com.mars.app.domain.comment.community.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.mars.app.domain.comment.community.dto.CommunityCommentRequestDto;
import com.mars.app.domain.comment.community.dto.CommunityCommentResponseDto;
import com.mars.app.domain.comment.community.repository.CommunityCommentRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.exception.NPGException;
import com.mars.common.model.comment.community.CommunityComment;
import com.mars.common.model.community.Community;
import com.mars.common.model.user.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CommunityCommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommunityCommentRepository communityCommentRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityCommentService communityCommentService;

    @DisplayName("게시글의 모든 댓글을 조회할 수 있다.")
    @Test
    void pagedCommentsByCommunity() {
        // given
        int pageNo = 0;
        int pageSize = 3;

        User user = createUser("dummy@nangpago.com");
        Community community = createCommunity(user);
        List<CommunityComment> comments = Arrays.asList(
            createCommunityComment(community, user, "1번째 댓글"),
            createCommunityComment(community, user, "2번째 댓글"),
            createCommunityComment(community, user, "3번째 댓글"),
            createCommunityComment(community, user, "4번째 댓글")
        );

        userRepository.save(user);
        communityRepository.save(community);
        communityCommentRepository.saveAll(comments);

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 3);

        // when
        PageResponseDto<CommunityCommentResponseDto> pageDto = communityCommentService.pagedCommentsByCommunity(
            community.getId(), user.getId(), pageRequestVO);


        // then
        assertThat(pageDto)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(2, 4L);
    }

    @DisplayName("게시글에 댓글을 작성할 수 있다.")
    @Test
    void create() {
        // given
        User user = createUser("dummy@nangpago.com");
        Community community = createCommunity(user);

        userRepository.save(user);
        communityRepository.save(community);

        CommunityCommentRequestDto requestDto = new CommunityCommentRequestDto("댓글 작성 예시");

        // when
        CommunityCommentResponseDto responseDto = communityCommentService.create(requestDto, user.getId(), community.getId());

        // then
        assertThat(responseDto)
            .extracting("content", "isOwnedByUser")
            .containsExactly("댓글 작성 예시", true);
    }

    @DisplayName("게시글의 댓글을 수정할 수 있다.")
    @Test
    void update() {
        // given
        User user = createUser("dummy@nangpago.com");
        Community community = createCommunity(user);
        CommunityComment comment = createCommunityComment(community, user, "변경 전 댓글입니다.");

        userRepository.save(user);
        communityRepository.save(community);
        communityCommentRepository.save(comment);

        String updateText = "변경된 댓글입니다.";
        CommunityCommentRequestDto requestDto = new CommunityCommentRequestDto(updateText);

        // when
        CommunityCommentResponseDto responseDto = communityCommentService.update(comment.getId(), user.getId(), requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).extracting("content").isEqualTo(updateText);
    }

    @DisplayName("게시글의 댓글을 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        User user = createUser("dummy@nangpago.com");
        Community community = createCommunity(user);
        CommunityComment comment = createCommunityComment(community, user, "댓글");

        userRepository.save(user);
        communityRepository.save(community);
        communityCommentRepository.save(comment);

        // when
        communityCommentService.delete(comment.getId(), user.getId());

        // then
        assertThat(communityCommentRepository.existsById(comment.getId()))
            .isFalse();
    }

    @DisplayName("다른 유저의 댓글을 수정/삭제 시도할 때 예외를 발생시킬 수 있다.")
    @Test
    void validateOwnershipException() {
        // given
        User user = createUser("dummy@nangpago.com");
        Community community = createCommunity(user);
        CommunityComment comment = createCommunityComment(community, user, "댓글");

        userRepository.save(user);
        communityRepository.save(community);
        communityCommentRepository.save(comment);

        Long anotherUserId = 999L;

        // when, then
        assertThatThrownBy(() -> communityCommentService.delete(comment.getId(), anotherUserId))
            .isInstanceOf(NPGException.class)
            .hasMessage("댓글을 수정/삭제할 권한이 없습니다.");
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }

    private Community createCommunity(User user) {
        return Community.builder()
            .user(user)
            .title("Test Community")
            .content("Test Content")
            .isPublic(true)
            .build();
    }

    private CommunityComment createCommunityComment(Community community, User user, String comment) {
        return CommunityComment.create(community, user, comment);
    }
}
