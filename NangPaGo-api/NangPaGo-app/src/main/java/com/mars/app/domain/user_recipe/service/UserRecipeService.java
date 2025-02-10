package com.mars.app.domain.user_recipe.service;

import static com.mars.common.exception.NPGExceptionType.*;
import com.mars.app.domain.user_recipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.user_recipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeCommentRepository;
import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.enums.userRecipe.UserRecipeStatus;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.model.userRecipe.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
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

    public PageResponseDto<UserRecipeResponseDto> getPagedUserRecipes(PageRequestVO pageRequestVO, Long userId) {
        Pageable pageable = pageRequestVO.toPageable();
        return PageResponseDto.of(
            userRecipeRepository.findByIsPublicTrueOrUserIdAndRecipeStatus(userId, pageable)
                .map(recipe -> {
                    int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
                    int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(recipe.getId());
                    return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
                })
        );
    }

    @Transactional(readOnly = true)
    public UserRecipeResponseDto getPostForEdit(Long id, Long userId) {
        UserRecipe recipe = getUserRecipe(id);
        validateOwnership(recipe, userId);

        int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
        int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(recipe.getId());

        return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
    }

    @Transactional
    public void deleteUserRecipe(Long id, Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);
        validateOwnership(userRecipe, userId);
        userRecipe.softDelete();
        userRecipeRepository.save(userRecipe);
    }

    private UserRecipe getUserRecipe(Long id) {
        return userRecipeRepository.findByIdAndRecipeStatus(id, UserRecipeStatus.ACTIVE)
            .orElseThrow(NOT_FOUND_RECIPE::of);
    }

    private void validateOwnership(UserRecipe userRecipe, Long userId) {
        if (!userRecipe.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    @Transactional
    public UserRecipeResponseDto createUserRecipe(UserRecipeRequestDto requestDto,
                                                  MultipartFile mainFile,
                                                  List<MultipartFile> otherFiles,
                                                  Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String mainImageUrl = getUploadedImage(mainFile, UserRecipeResponseDto.DEFAULT_IMAGE_URL);

        UserRecipe recipe = userRecipeRepository.save(UserRecipe.builder()
            .user(user)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .mainImageUrl(mainImageUrl)
            .isPublic(requestDto.isPublic())
            .recipeStatus(UserRecipeStatus.ACTIVE)
            .build());

        recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));
        recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));

        return UserRecipeResponseDto.of(userRecipeRepository.save(recipe), 0, 0, userId);
    }

    private String getUploadedImage(MultipartFile file, String existingImageUrl) {
        if (file != null && !file.isEmpty()) {
            return firebaseStorageService.uploadNewFile(file);
        }
        return (existingImageUrl == null || existingImageUrl.isBlank())
            ? UserRecipeResponseDto.DEFAULT_IMAGE_URL
            : existingImageUrl;
    }

    @Transactional
    public UserRecipeResponseDto updateUserRecipe(Long id, UserRecipeRequestDto requestDto,
        MultipartFile mainFile, List<MultipartFile> otherFiles,
        Long userId) {
        UserRecipe recipe = userRecipeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        String previousMainImageUrl = recipe.getMainImageUrl();

        String mainImageUrl = (mainFile != null && !mainFile.isEmpty()) ?
            firebaseStorageService.updateFile(mainFile, previousMainImageUrl) : previousMainImageUrl;

        recipe.update(requestDto.getTitle(), requestDto.getContent(), requestDto.isPublic(), mainImageUrl);

        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));

        recipe.getManuals().clear();
        recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));

        return UserRecipeResponseDto.of(userRecipeRepository.save(recipe), 0, 0, userId);
    }

    private List<UserRecipeIngredient> buildIngredients(UserRecipeRequestDto dto, UserRecipe recipe) {
        return dto.getIngredients().stream()
            .map(ing -> UserRecipeIngredient.builder()
                .userRecipe(recipe)
                .name(ing.getName())
                .amount(ing.getAmount())
                .build())
            .collect(Collectors.toList());
    }

    private List<UserRecipeManual> buildManuals(UserRecipeRequestDto dto, List<MultipartFile> otherFiles, UserRecipe recipe) {
        return dto.getManuals().stream()
            .map(manualDto -> {
                int stepValue = manualDto.getStep();
                if (stepValue < 1) {
                    stepValue = 1;
                }
                int index = stepValue - 1;

                String imageUrl = getUploadedImage(
                    (otherFiles != null && index < otherFiles.size()) ? otherFiles.get(index) : null,
                    manualDto.getImageUrl()
                );

                return UserRecipeManual.builder()
                    .userRecipe(recipe)
                    .step(stepValue)
                    .description(manualDto.getDescription())
                    .imageUrl(imageUrl)
                    .build();
            })
            .collect(Collectors.toList());
    }

}
