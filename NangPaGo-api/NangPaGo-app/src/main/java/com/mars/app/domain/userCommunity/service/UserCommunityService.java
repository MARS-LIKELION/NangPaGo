package com.mars.app.domain.userCommunity.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.common.dto.PageDto;
import com.mars.app.domain.userCommunity.dto.UserCommunityRequestDto;
import com.mars.app.domain.userCommunity.dto.UserCommunityResponseDto;
import com.mars.app.domain.userCommunity.repository.UserCommunityLikeRepository;
import com.mars.app.domain.userCommunity.repository.UserCommunityRepository;
import com.mars.app.domain.comment.userCommunity.repository.UserCommunityCommentRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.model.userCommunity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCommunityService {

    private final UserCommunityRepository userCommunityRepository;
    private final UserCommunityLikeRepository userCommunityLikeRepository;
    private final UserCommunityCommentRepository userCommunityCommentRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;

    public UserCommunityResponseDto getUserCommunityById(Long id, Long userId) {
        UserCommunity userCommunity = getUserCommunity(id);

        if (userCommunity.isPrivate()) {
            validateOwnership(userCommunity, userId);
        }

        int likeCount = (int) userCommunityLikeRepository.countByUserCommunityId(userCommunity.getId());
        int commentCount = (int) userCommunityCommentRepository.countByUserCommunityId(userCommunity.getId());

        return UserCommunityResponseDto.of(userCommunity, likeCount, commentCount, userId);
    }


    public PageDto<UserCommunityResponseDto> pagesByUserCommunity(int pageNo, int pageSize, Long userId) {
        Pageable pageable = createPageRequest(pageNo, pageSize);

        return PageDto.of(
            userCommunityRepository.findByIsPublicTrueOrUserId(userId, pageable)
                .map(userCommunity -> {
                    int likeCount = (int) userCommunityLikeRepository.countByUserCommunityId(userCommunity.getId());
                    int commentCount = (int) userCommunityCommentRepository.countByUserCommunityId(userCommunity.getId());
                    return UserCommunityResponseDto.of(userCommunity, likeCount, commentCount, userId);
                })
        );
    }

    @Transactional
    public UserCommunityResponseDto createUserCommunity(UserCommunityRequestDto requestDto, MultipartFile mainFile, List<MultipartFile> otherFiles, Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        // 2. 대표 이미지 업로드 또는 기본 이미지 URL 사용
        String mainImageUrl;
        if (mainFile != null && !mainFile.isEmpty()) {
            mainImageUrl = firebaseStorageService.uploadFile(mainFile);
        } else {
            // 대표 이미지가 제공되지 않은 경우, 기존 community에서 사용하던 기본 이미지 URL을 할당
            mainImageUrl = UserCommunityResponseDto.DEFAULT_IMAGE_URL;
        }

        // 3. UserCommunity 엔티티 생성 (연관 컬렉션들을 빈 리스트로 초기화)
        UserCommunity userCommunity = UserCommunity.builder()
            .user(user)
            .title(requestDto.title())
            .content(requestDto.content())
            .mainImageUrl(mainImageUrl)
            .isPublic(requestDto.isPublic())
            .ingredients(new ArrayList<>())
            .manuals(new ArrayList<>())
            .comments(new ArrayList<>())
            .likes(new ArrayList<>())
            .build();

        // 4. 재료(ingredients) 추가
        List<UserCommunityIngredient> ingredients = requestDto.ingredients().stream()
            .map(ingredient -> UserCommunityIngredient.builder()
                .userCommunity(userCommunity)
                .name(ingredient)
                .amount("") // 기본 값
                .build())
            .collect(Collectors.toList());
        userCommunity.getIngredients().addAll(ingredients);

        // 5. 조리 과정(manuals) 및 조리 과정 이미지 추가
        List<UserCommunityManual> manuals = new ArrayList<>();
        for (int i = 0; i < requestDto.manuals().size(); i++) {
            String manualText = requestDto.manuals().get(i);
            // 각 조리 과정 엔티티 생성 시 images 컬렉션은 빈 리스트로 초기화
            UserCommunityManual manual = UserCommunityManual.builder()
                .userCommunity(userCommunity)
                .step(i + 1)
                .description(manualText)
                .images(new ArrayList<>())
                .build();

            // otherFiles의 i번째 파일을 해당 조리 과정 이미지로 처리 (파일이 없으면 건너뜀)
            if (otherFiles != null && otherFiles.size() > i && otherFiles.get(i) != null && !otherFiles.get(i).isEmpty()) {
                String imageUrl = firebaseStorageService.uploadFile(otherFiles.get(i));
                UserCommunityManualImage manualImage = UserCommunityManualImage.builder()
                    .userCommunityManual(manual)
                    .imageUrl(imageUrl)
                    .build();
                manual.getImages().add(manualImage);
            }
            manuals.add(manual);
        }
        userCommunity.getManuals().addAll(manuals);

        // 6. 엔티티 저장 (한 번의 save 호출)
        userCommunityRepository.save(userCommunity);

        return UserCommunityResponseDto.of(userCommunity, 0, 0, userId);
    }


    @Transactional
    public UserCommunityResponseDto updateUserCommunity(Long id, UserCommunityRequestDto requestDto,
                                                        MultipartFile mainFile, List<MultipartFile> otherFiles,
                                                        Long userId) {
        // 1. 수정할 게시글 조회 및 소유자 검증
        UserCommunity userCommunity = getUserCommunity(id);
        validateOwnership(userCommunity, userId);

        // 2. 대표 이미지 업데이트 (새 대표 이미지가 있으면 업로드, 없으면 기존 이미지 유지)
        String mainImageUrl = userCommunity.getMainImageUrl();
        if (mainFile != null && !mainFile.isEmpty()) {
            mainImageUrl = firebaseStorageService.uploadFile(mainFile);
        } else if (mainImageUrl == null || mainImageUrl.isBlank()) {
            // 만약 기존 대표 이미지가 null 또는 빈 값이면 기본 이미지 URL을 사용
            mainImageUrl = UserCommunityResponseDto.DEFAULT_IMAGE_URL;
        }

        // 3. 제목, 내용, 공개 여부, 대표 이미지 업데이트
        userCommunity.update(
            requestDto.title(),
            requestDto.content(),
            requestDto.isPublic(),
            mainImageUrl
        );

        // 4. 메뉴얼(조리 과정 텍스트)와 조리 과정 이미지 업데이트
        //    새로운 메뉴얼 목록이 전달되었다면 기존 메뉴얼(및 관련 이미지)을 모두 대체
        if (requestDto.manuals() != null && !requestDto.manuals().isEmpty()) {
            // 기존 메뉴얼 컬렉션을 비움 (orphanRemoval에 의해 DB에서도 삭제됨)
            userCommunity.getManuals().clear();

            List<UserCommunityManual> updatedManuals = new ArrayList<>();
            for (int i = 0; i < requestDto.manuals().size(); i++) {
                String manualText = requestDto.manuals().get(i);
                // 각 메뉴얼 생성 시 images 컬렉션은 빈 리스트로 초기화
                UserCommunityManual manual = UserCommunityManual.builder()
                    .userCommunity(userCommunity)
                    .step(i + 1)
                    .description(manualText)
                    .images(new ArrayList<>())
                    .build();

                // otherFiles에서 해당 인덱스에 파일이 있다면, 조리 과정 이미지로 업로드 및 추가
                if (otherFiles != null && otherFiles.size() > i
                    && otherFiles.get(i) != null && !otherFiles.get(i).isEmpty()) {
                    String imageUrl = firebaseStorageService.uploadFile(otherFiles.get(i));
                    UserCommunityManualImage manualImage = UserCommunityManualImage.builder()
                        .userCommunityManual(manual)
                        .imageUrl(imageUrl)
                        .build();
                    manual.getImages().add(manualImage);
                }
                updatedManuals.add(manual);
            }
            // 새 메뉴얼 목록을 기존 메뉴얼 컬렉션에 추가
            userCommunity.getManuals().addAll(updatedManuals);
        }

        // 5. 변경된 내용 저장 (업데이트된 엔티티는 자동으로 DB에 반영됨)
        userCommunityRepository.save(userCommunity);

        return UserCommunityResponseDto.of(userCommunity, 0, 0, userId);
    }




    @Transactional
    public void deleteUserCommunity(Long id, Long userId) {
        UserCommunity userCommunity = getUserCommunity(id);
        validateOwnership(userCommunity, userId);
        userCommunityRepository.deleteById(id);
    }

    private UserCommunity getUserCommunity(Long id) {
        return userCommunityRepository.findById(id)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }

    private void validateOwnership(UserCommunity userCommunity, Long userId) {
        if (!userCommunity.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
