package com.mars.app.domain.userRecipe.service;

import static com.mars.common.exception.NPGExceptionType.*;
import com.mars.app.domain.userRecipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.userRecipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.userRecipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.userRecipe.repository.UserRecipeRepository;
import com.mars.app.domain.comment.userRecipe.repository.UserRecipeCommentRepository;
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

import java.util.ArrayList;
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
            userRecipeRepository.findByIsPublicTrueOrUserId(userId, pageable)
                .map(recipe -> {
                    int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
                    int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(recipe.getId());
                    return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
                })
        );
    }

    @Transactional
    public UserRecipeResponseDto createUserRecipe(UserRecipeRequestDto requestDto,
                                                  MultipartFile mainFile,
                                                  List<MultipartFile> otherFiles,
                                                  Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        String mainImageUrl = getMainImageUrl(mainFile);

        UserRecipe recipe = UserRecipe.builder()
            .user(user)
            .title(requestDto.title())
            .content(requestDto.content())
            .mainImageUrl(mainImageUrl)
            .isPublic(requestDto.isPublic())
            .recipeStatus(UserRecipeStatus.ACTIVE)
            .ingredients(new ArrayList<>())
            .manuals(new ArrayList<>())
            .comments(new ArrayList<>())
            .likes(new ArrayList<>())
            .build();

        recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));
        recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));

        userRecipeRepository.save(recipe);
        return UserRecipeResponseDto.of(recipe, 0, 0, userId);
    }

    @Transactional
    public UserRecipeResponseDto updateUserRecipe(Long id, UserRecipeRequestDto requestDto,
                                                  MultipartFile mainFile, List<MultipartFile> otherFiles,
                                                  Long userId) {
        UserRecipe recipe = getUserRecipe(id);
        validateOwnership(recipe, userId);

        String mainImageUrl = updateMainImage(recipe.getMainImageUrl(), mainFile);
        recipe.update(requestDto.title(), requestDto.content(), requestDto.isPublic(), mainImageUrl);

        if (requestDto.ingredients() != null && !requestDto.ingredients().isEmpty()) {
            recipe.getIngredients().clear();
            recipe.getIngredients().addAll(buildIngredients(requestDto, recipe));
        }

        if (requestDto.manuals() != null && !requestDto.manuals().isEmpty()) {
            recipe.getManuals().clear();
            recipe.getManuals().addAll(buildManuals(requestDto, otherFiles, recipe));
        }
        userRecipeRepository.save(recipe);

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

    public UserRecipeResponseDto getPostForEdit(Long id, Long userId) {
        UserRecipe recipe = getUserRecipe(id);
        validateOwnership(recipe, userId);
        int likeCount = (int) userRecipeLikeRepository.countByUserRecipeId(recipe.getId());
        int commentCount = (int) userRecipeCommentRepository.countByUserRecipeId(recipe.getId());
        return UserRecipeResponseDto.of(recipe, likeCount, commentCount, userId);
    }

    private String getMainImageUrl(MultipartFile mainFile) {
        return (mainFile != null && !mainFile.isEmpty())
            ? firebaseStorageService.uploadFile(mainFile)
            : UserRecipeResponseDto.DEFAULT_IMAGE_URL;
    }

    private String updateMainImage(String existing, MultipartFile mainFile) {
        if (mainFile != null && !mainFile.isEmpty()) {
            return firebaseStorageService.uploadFile(mainFile);
        }
        return (existing == null || existing.isBlank())
            ? UserRecipeResponseDto.DEFAULT_IMAGE_URL
            : existing;
    }

    private List<UserRecipeIngredient> buildIngredients(UserRecipeRequestDto dto, UserRecipe recipe) {
        return dto.ingredients().stream()
            .map(ing -> UserRecipeIngredient.builder()
                .userRecipe(recipe)
                .name(ing)
                .amount("")
                .build())
            .collect(Collectors.toList());
    }

    private List<UserRecipeManual> buildManuals(UserRecipeRequestDto dto, List<MultipartFile> otherFiles, UserRecipe recipe) {
        List<UserRecipeManual> manuals = new ArrayList<>();
        for (int i = 0; i < dto.manuals().size(); i++) {
            UserRecipeManual manual = UserRecipeManual.builder()
                .userRecipe(recipe)
                .step(i + 1)
                .description(dto.manuals().get(i))
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
        return manuals;
    }
}
