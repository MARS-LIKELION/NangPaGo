package com.mars.app.domain.user_recipe.service;

import static com.mars.app.domain.user_recipe.dto.UserRecipeResponseDto.DEFAULT_IMAGE_URL;
import static com.mars.common.enums.userRecipe.UserRecipeStatus.ACTIVE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static java.util.Objects.requireNonNullElse;

import com.mars.app.domain.firebase.service.FirebaseStorageService;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.domain.user_recipe.dto.UserRecipeListResponseDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.user_recipe.repository.UserRecipeCommentRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.model.user.User;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.userRecipe.UserRecipeIngredient;
import com.mars.common.model.userRecipe.UserRecipeLike;
import com.mars.common.model.userRecipe.UserRecipeManual;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserRecipeService {

    public static final int DEFAULT_LIKE_COUNT = 0;
    public static final int DEFAULT_COMMENT_COUNT = 0;
    public static final int MIN_STEP = 0;
    public static final int STEP_OFFSET = 1;

    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipeLikeRepository userRecipeLikeRepository;
    private final UserRecipeCommentRepository userRecipeCommentRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Transactional
    public UserRecipeResponseDto createUserRecipe(UserRecipeRequestDto requestDto,
                                                    MultipartFile mainFile,
                                                    List<MultipartFile> otherFiles,
                                                    Long userId
    ) {
        User user = validateUser(userId);
        UserRecipe recipe = createRecipe(requestDto, mainFile, user);

        recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));
        recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));

        return UserRecipeResponseDto.of(userRecipeRepository.save(recipe), DEFAULT_LIKE_COUNT,
            DEFAULT_COMMENT_COUNT, userId);
    }

    public UserRecipeResponseDto getUserRecipeById(Long id, Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);

        if (userRecipe.isPrivate()) {
            validateOwnership(userRecipe, userId);
        }

        int likeCount = userRecipeLikeRepository.countByUserRecipeId(userRecipe.getId());
        int commentCount = userRecipeCommentRepository.countByUserRecipeId(userRecipe.getId());

        return UserRecipeResponseDto.of(userRecipe, likeCount, commentCount, userId);
    }

    public PageResponseDto<UserRecipeListResponseDto> getPagedUserRecipes(Long userId, PageRequestVO pageRequestVO) {
        return PageResponseDto.of(
            userRecipeRepository.findByIsPublicTrueOrUserIdAndRecipeStatus(userId, pageRequestVO.toPageable())
                .map(userRecipe -> toUserRecipeListResponseDto(userRecipe, userId))
        );
    }

    @Transactional(readOnly = true)
    public UserRecipeResponseDto getPostForEdit(Long id, Long userId) {
        UserRecipe recipe = getUserRecipe(id);
        validateOwnership(recipe, userId);

        int likeCount = userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
        int commentCount = userRecipeCommentRepository.countByUserRecipeId(recipe.getId());

        return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
    }

    @Transactional
    public UserRecipeResponseDto updateUserRecipe(Long id, UserRecipeRequestDto requestDto, MultipartFile mainFile,
                                                    List<MultipartFile> otherFiles, Long userId
    ) {
        UserRecipe recipe = getUserRecipe(id);
        validateOwnership(recipe, userId);

        String mainImageUrl = updateMainImage(mainFile, recipe.getMainImageUrl());
        recipe.update(requestDto.getTitle(), requestDto.getContent(), requestDto.isPublic(), mainImageUrl);

        deleteUnusedManualImages(recipe, requestDto);

        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));

        recipe.getManuals().clear();
        recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));

        return UserRecipeResponseDto.of(userRecipeRepository.save(recipe), DEFAULT_LIKE_COUNT, DEFAULT_COMMENT_COUNT, userId);
    }

    @Transactional
    public void deleteUserRecipe(Long id, Long userId) {
        UserRecipe userRecipe = getUserRecipe(id);
        validateOwnership(userRecipe, userId);

        deleteImage(userRecipe.getMainImageUrl());

        userRecipe.getManuals().stream()
            .map(UserRecipeManual::getImageUrl)
            .forEach(this::deleteImage);

        userRecipe.softDelete();
        userRecipeRepository.save(userRecipe);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> NOT_FOUND_USER.of("유저를 찾을 수 없습니다."));
    }

    private UserRecipe createRecipe(UserRecipeRequestDto requestDto, MultipartFile mainFile, User user) {
        return userRecipeRepository.save(UserRecipe.builder()
            .user(user)
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .mainImageUrl(getUploadedImage(mainFile, DEFAULT_IMAGE_URL))
            .isPublic(requestDto.isPublic())
            .recipeStatus(ACTIVE)
            .build());
    }

    private String getUploadedImage(MultipartFile file, String existingImageUrl) {
        if (file != null && !file.isEmpty()) {
            return firebaseStorageService.uploadNewFile(file);
        }
        return requireNonNullElse(existingImageUrl, DEFAULT_IMAGE_URL);
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
            .map(manualDto -> createManual(manualDto, getFileByIndex(otherFiles, manualDto.getStep()), recipe))
            .collect(Collectors.toList());
    }

    private UserRecipeManual createManual(UserRecipeRequestDto.ManualDto manualDto, MultipartFile file, UserRecipe recipe) {
        return UserRecipeManual.builder()
            .userRecipe(recipe)
            .step(manualDto.getStep())
            .description(manualDto.getDescription())
            .imageUrl(getUploadedImage(file, manualDto.getImageUrl()))
            .build();
    }

    private MultipartFile getFileByIndex(List<MultipartFile> files, int step) {
        if (files == null || step <= MIN_STEP || step - STEP_OFFSET >= files.size()) {
            return null;
        }
        return files.get(step - STEP_OFFSET);
    }

    private void validateOwnership(UserRecipe userRecipe, Long userId) {
        if (!userRecipe.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private UserRecipeListResponseDto toUserRecipeListResponseDto(UserRecipe userRecipe, Long userId) {
        return UserRecipeListResponseDto.of(
            userRecipe,
            userRecipeLikeRepository.countByUserRecipeId(userRecipe.getId()),
            userRecipeCommentRepository.countByUserRecipeId(userRecipe.getId()),
            getUserRecipeLikesBy(userId)
        );
    }

    private UserRecipe getUserRecipe(Long id) {
        return userRecipeRepository.findByIdAndRecipeStatus(id, ACTIVE)
            .orElseThrow(NOT_FOUND_RECIPE::of);
    }

    private List<UserRecipeLike> getUserRecipeLikesBy(Long userId) {
        return userId.equals(User.ANONYMOUS_USER_ID)
            ? new ArrayList<>() : userRecipeLikeRepository.findUserRecipeLikesByUserId(userId);
    }

    private String updateMainImage(MultipartFile mainFile, String previousMainImageUrl) {
        if (mainFile != null && !mainFile.isEmpty()) {
            return firebaseStorageService.updateFile(mainFile, previousMainImageUrl);
        }
        return previousMainImageUrl;
    }

    private void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            firebaseStorageService.deleteFileFromFirebase(imageUrl);
        }
    }

    private void deleteUnusedManualImages(UserRecipe recipe, UserRecipeRequestDto requestDto) {
        List<String> oldManualImages = recipe.getManuals().stream()
            .map(UserRecipeManual::getImageUrl)
            .collect(Collectors.toList());

        List<String> newManualImages = requestDto.getManuals().stream()
            .map(UserRecipeRequestDto.ManualDto::getImageUrl)
            .collect(Collectors.toList());

        oldManualImages.stream()
            .filter(image -> isUnusedImage(image, newManualImages))
            .forEach(firebaseStorageService::deleteFileFromFirebase);
    }

    private boolean isUnusedImage(String imageUrl, List<String> newImages) {
        return imageUrl != null && !imageUrl.isBlank() && !newImages.contains(imageUrl);
    }

}
