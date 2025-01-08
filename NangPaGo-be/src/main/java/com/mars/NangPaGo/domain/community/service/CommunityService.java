package com.mars.NangPaGo.domain.community.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.domain.community.dto.CommunityRequestDto;
import com.mars.NangPaGo.domain.community.dto.CommunityResponseDto;
import com.mars.NangPaGo.domain.community.entity.Community;
import com.mars.NangPaGo.domain.community.repository.CommunityRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public PageDto<CommunityResponseDto> pagesByCommunity(int pageNo, int pageSize, String email) {
        Pageable pageable = createPageRequest(pageNo, pageSize);
        return PageDto.of(
            ("anonymous_user".equals(email) ?
                communityRepository.findByIsPublicTrue(pageable) :
                communityRepository.findByIsPublicTrueOrUserEmail(email, pageable))
                .map(community -> CommunityResponseDto.of(community, email))
        );
    }

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, String email) {
        User user = findUserByEmail(email);
        Community community = Community.of(user, requestDto.title(), requestDto.content(), requestDto.isPublic());
        communityRepository.save(community);
        return CommunityResponseDto.of(community, email);
    }

    @Transactional
    public CommunityResponseDto updateCommunity(Long id, CommunityRequestDto requestDto, String email) {
        Community community = validateCommunity(id);
        validateOwnership(community, email);
        community.update(requestDto.title(), requestDto.content(), requestDto.isPublic());
        return CommunityResponseDto.of(community, email);
    }

    @Transactional
    public void deleteCommunity(Long id, String email) {
        Community community = validateCommunity(id);
        validateOwnership(community, email);
        communityRepository.deleteById(id);
    }

    private void validateOwnership(Community community, String email) {
        if (!community.getUser().getEmail().equals(email)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private Community validateCommunity(Long id) {
        return communityRepository.findById(id)
            .orElseThrow(() -> NOT_FOUND_COMMUNITY.of());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of());
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
