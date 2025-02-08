package com.mars.app.domain.recipe.controller.like;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.recipe.dto.like.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.event.RecipeLikeSseService;
import com.mars.app.domain.recipe.message.like.RecipeLikeMessagePublisher;
import com.mars.app.domain.recipe.service.like.RecipeLikeService;
import com.mars.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Tag(name = "레시피 좋아요 API", description = "공공 데이터 레시피 좋아요, SSE 구독")
@RequestMapping("/api/recipe")
@RestController
public class RecipeLikeController {

    private final RecipeLikeService recipeLikeService;
    private final RecipeLikeMessagePublisher recipeLikeMessagePublisher;
    private final RecipeLikeSseService recipeLikeSseService;

    @Operation(summary = "레시피 총 좋아요 개수 조회")
    @GetMapping("/{id}/like/count")
    public ResponseDto<Integer> getLikeCount(@PathVariable Long id) {
        return ResponseDto.of(recipeLikeService.getLikeCount(id));
    }

    @Operation(summary = "좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/{id}/like/status")
    public ResponseDto<Boolean> getRecipeLikeStatus(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeLikeService.isLiked(id, userId));
    }

    @Operation(summary = "좋아요 상태 변경(Toggle)")
    @AuthenticatedUser
    @PostMapping("/{id}/like/toggle")
    public ResponseDto<RecipeLikeResponseDto> toggleRecipeLike(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeLikeMessagePublisher.toggleLike(id, userId));
    }

    @Operation(summary = "레시피 총 좋아요 개수 변경 SSE 이벤트 구독")
    @GetMapping("/{id}/like/notification/subscribe")
    public SseEmitter streamLikes(@PathVariable Long id) {
        return recipeLikeSseService.createEmitter(id);
    }
}
