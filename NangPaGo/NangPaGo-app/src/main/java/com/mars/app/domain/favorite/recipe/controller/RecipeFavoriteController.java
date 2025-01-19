package com.mars.app.domain.favorite.recipe.controller;

import com.mars.app.common.dto.PageDto;
import com.mars.app.common.dto.ResponseDto;
import com.mars.app.common.aop.auth.AuthenticatedUser;
import com.mars.app.common.component.auth.AuthenticationHolder;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.app.domain.favorite.recipe.service.RecipeFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "레시피 즐겨찾기 API", description = "레시피 즐겨찾기 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
@RestController
public class RecipeFavoriteController {

    private final RecipeFavoriteService recipeFavoriteService;

    @Operation(summary = "즐겨찾기 상태 확인")
    @GetMapping("/{id}/favorite/status")
    public ResponseEntity<Boolean> isFavorite(@PathVariable("id") Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseEntity.ok(recipeFavoriteService.isFavorite(id, email));
    }

    @Operation(summary = "즐겨찾기 상태 변경")
    @AuthenticatedUser
    @PostMapping("/{id}/favorite/toggle")
    public ResponseDto<RecipeFavoriteResponseDto> toggleFavorite(@PathVariable("id") Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeFavoriteService.toggleFavorite(id, email));
    }

    @Operation(summary = "즐겨찾기 목록 조회")
    @AuthenticatedUser
    @GetMapping("/favorite/list")
    public ResponseDto<PageDto<RecipeFavoriteListResponseDto>> getFavoriteRecipes(Pageable pageable) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        PageDto<RecipeFavoriteListResponseDto> favoriteRecipes = recipeFavoriteService.getFavoriteRecipes(email, pageable);
        return ResponseDto.of(favoriteRecipes);
    }
}
