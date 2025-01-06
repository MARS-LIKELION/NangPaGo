package com.mars.NangPaGo.domain.favorite.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;

import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.service.RecipeFavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Tag(name = "레시피 즐겨찾기 API", description = "레시피 즐겨찾기 관련 API")
@RequestMapping("/api/recipe")
@RestController
public class RecipeFavoriteController {

    private final RecipeFavoriteService recipeFavoriteService;

    @AuthenticatedUser
    @GetMapping("/{id}/favorite/status")
    public ResponseEntity<Boolean> isFavorite(@PathVariable("id") Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseEntity.ok(recipeFavoriteService.isFavorite(id, email));
    }

    @AuthenticatedUser
    @PostMapping("/{id}/favorite/toggle")
    public ResponseDto<RecipeFavoriteResponseDto> toggleFavorite(@PathVariable("id") Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeFavoriteService.toggleFavorite(id, email));
    }

    @AuthenticatedUser
    @GetMapping("/favorite/list")
    public ResponseDto<List<RecipeFavoriteListResponseDto>> getFavoriteRecipes(Principal principal) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        List<RecipeFavoriteListResponseDto> favoriteRecipes = recipeFavoriteService.getFavoriteRecipes(email);
        return ResponseDto.of(favoriteRecipes);
    }
}
