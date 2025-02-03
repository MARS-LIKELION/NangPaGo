package com.mars.app.domain.userRecipe.service;

import static com.mars.common.exception.NPGExceptionType.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import com.mars.common.dto.PageDto;
import com.mars.app.domain.userRecipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.userRecipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.userRecipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.userRecipe.repository.UserRecipeRepository;
import com.mars.app.domain.comment.userRecipe.repository.UserRecipeCommentRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.model.userRecipe.*;
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

@Transactional
@RequiredArgsConstructor
@Service
public class UserRecipeService {

    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipeLikeRepository userRecipeLikeRepository;
    private final UserRecipeCommentRepository userRecipeCommentRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;

    public UserRecipeResponseDto getUserRecipeById(Long id, Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);

        if (userRecipe.isPrivate()) {
            validateOwnership(userRecipe, userId);
        }

        int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(userRecipe.getId());
        int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(userRecipe.getId());

        return UserRecipeResponseDto.of(userRecipe, likeCount, commentCount, userId);
    }

    public PageDto<UserRecipeResponseDto> getPagedUserRecipes(int pageNo, int pageSize, Long userId) {
        Pageable pageable = createPageRequest(pageNo, pageSize);

        return PageDto.of(
            userRecipeRepository.findByIsPublicTrueOrUserId(userId, pageable)
                .map(recipe -> {
                    int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
                    int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(recipe.getId());
                    return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
                })
        );
    }

    @Transactional
    public UserRecipeResponseDto createUserRecipe(UserRecipeRequestDto requestDto, MultipartFile mainFile, List<MultipartFile> otherFiles, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        String mainImageUrl = (mainFile != null && !mainFile.isEmpty())
            ? firebaseStorageService.uploadFile(mainFile)
            : UserRecipeResponseDto.DEFAULT_IMAGE_URL;

        UserRecipe userRecipe = UserRecipe.builder()
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

        List<UserRecipeIngredient> ingredients = requestDto.ingredients().stream()
            .map(ingredient -> UserRecipeIngredient.builder()
                .userRecipe(userRecipe)
                .name(ingredient)
                .amount("")
                .build())
            .collect(Collectors.toList());
        userRecipe.getIngredients().addAll(ingredients);

        List<UserRecipeManual> manuals = new ArrayList<>();
        for (int i = 0; i < requestDto.manuals().size(); i++) {
            String manualText = requestDto.manuals().get(i);
            UserRecipeManual manual = UserRecipeManual.builder()
                .userRecipe(userRecipe)
                .step(i + 1)
                .description(manualText)
                .images(new ArrayList<>())
                .build();

            if (otherFiles != null && otherFiles.size() > i && otherFiles.get(i) != null && !otherFiles.get(i).isEmpty()) {
                String imageUrl = firebaseStorageService.uploadFile(otherFiles.get(i));
                UserRecipeManualImage manualImage = UserRecipeManualImage.builder()
                    .userRecipeManual(manual)
                    .imageUrl(imageUrl)
                    .build();
                manual.getImages().add(manualImage);
            }
            manuals.add(manual);
        }
        userRecipe.getManuals().addAll(manuals);

        userRecipeRepository.save(userRecipe);

        return UserRecipeResponseDto.of(userRecipe, 0, 0, userId);
    }

    @Transactional
    public UserRecipeResponseDto updateUserRecipe(Long id, UserRecipeRequestDto requestDto,
                                                  MultipartFile mainFile, List<MultipartFile> otherFiles,
                                                  Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);
        validateOwnership(userRecipe, userId);

        String mainImageUrl = userRecipe.getMainImageUrl();
        if (mainFile != null && !mainFile.isEmpty()) {
            mainImageUrl = firebaseStorageService.uploadFile(mainFile);
        } else if (mainImageUrl == null || mainImageUrl.isBlank()) {
            mainImageUrl = UserRecipeResponseDto.DEFAULT_IMAGE_URL;
        }

        userRecipe.update(
            requestDto.title(),
            requestDto.content(),
            requestDto.isPublic(),
            mainImageUrl
        );

        if (requestDto.manuals() != null && !requestDto.manuals().isEmpty()) {
            userRecipe.getManuals().clear();
            List<UserRecipeManual> updatedManuals = new ArrayList<>();
            for (int i = 0; i < requestDto.manuals().size(); i++) {
                String manualText = requestDto.manuals().get(i);
                UserRecipeManual manual = UserRecipeManual.builder()
                    .userRecipe(userRecipe)
                    .step(i + 1)
                    .description(manualText)
                    .images(new ArrayList<>())
                    .build();
                if (otherFiles != null && otherFiles.size() > i && otherFiles.get(i) != null && !otherFiles.get(i).isEmpty()) {
                    String imageUrl = firebaseStorageService.uploadFile(otherFiles.get(i));
                    UserRecipeManualImage manualImage = UserRecipeManualImage.builder()
                        .userRecipeManual(manual)
                        .imageUrl(imageUrl)
                        .build();
                    manual.getImages().add(manualImage);
                }
                updatedManuals.add(manual);
            }
            userRecipe.getManuals().addAll(updatedManuals);
        }
        userRecipeRepository.save(userRecipe);

        return UserRecipeResponseDto.of(userRecipe, 0, 0, userId);
    }

    @Transactional
    public void deleteUserRecipe(Long id, Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);
        validateOwnership(userRecipe, userId);
        userRecipeRepository.deleteById(id);
    }

    private UserRecipe getUserRecipe(Long id) {
        return userRecipeRepository.findById(id)
            .orElseThrow(NOT_FOUND_RECIPE::of);
    }

    private void validateOwnership(UserRecipe userRecipe, Long userId) {
        if (!userRecipe.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
