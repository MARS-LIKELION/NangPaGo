package com.mars.app.domain.community.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.common.dto.PageDto;
import com.mars.app.domain.comment.community.repository.CommunityCommentRepository;
import com.mars.app.domain.community.dto.CommunityRequestDto;
import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.common.model.community.Community;
import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;

    public CommunityResponseDto getCommunityById(Long id, String email) {
        Community community = getCommunity(id);

        if (community.isPrivate()) {
            validateOwnership(community, email);
        }

        return CommunityResponseDto.of(community, email);
    }

    public PageDto<CommunityResponseDto> pagesByCommunity(int pageNo, int pageSize, String email) {
        Pageable pageable = createPageRequest(pageNo, pageSize);

        return PageDto.of(
            ("anonymous_user".equals(email) ?
                communityRepository.findByIsPublicTrue(pageable) :
                communityRepository.findByIsPublicTrueOrUserEmail(email, pageable))
                .map(community -> {
                    int likeCount = communityLikeRepository.countByCommunityId(community.getId());
                    int commentCount = communityCommentRepository.countByCommunityId(community.getId());
                    return CommunityResponseDto.of(community, likeCount, commentCount, email);
                })
        );
    }

    public CommunityResponseDto getPostForEdit(Long id, String email) {
        Community community = getCommunity(id);
        validateOwnership(community, email);

        return CommunityResponseDto.of(community, email);
    }
  
    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, MultipartFile file, String email) {
        User user = findUserByEmail(email);

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = firebaseStorageService.uploadFile(file);
        }

        Community community = Community.of(
            user,
            requestDto.title(),
            requestDto.content(),
            imageUrl,
            requestDto.isPublic()
        );

        communityRepository.save(community);
        return CommunityResponseDto.of(community, email);
    }

    @Transactional
    public CommunityResponseDto updateCommunity(Long id, CommunityRequestDto requestDto, MultipartFile file, String email) {
        Community community = getCommunity(id);
        validateOwnership(community, email);

        String imageUrl = community.getImageUrl();
        if (file != null && !file.isEmpty()) {
            imageUrl = firebaseStorageService.uploadFile(file);
        }

        String updatedTitle = (requestDto.title() != null && !requestDto.title().isEmpty()) ? requestDto.title() : community.getTitle();
        String updatedContent = (requestDto.content() != null && !requestDto.content().isEmpty()) ? requestDto.content() : community.getContent();
        boolean updatedIsPublic = requestDto.isPublic();

        community.update(
            updatedTitle,
            updatedContent,
            updatedIsPublic,
            imageUrl
        );

        return CommunityResponseDto.of(community, email);
    }

    @Transactional
    public void deleteCommunity(Long id, String email) {
        Community community = getCommunity(id);
        validateOwnership(community, email);
        communityRepository.deleteById(id);
    }

    private Community getCommunity(Long id) {
        return communityRepository.findById(id)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }

    private void validateOwnership(Community community, String email) {
        if (!community.getUser().getEmail().equals(email)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(NOT_FOUND_USER::of);
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
