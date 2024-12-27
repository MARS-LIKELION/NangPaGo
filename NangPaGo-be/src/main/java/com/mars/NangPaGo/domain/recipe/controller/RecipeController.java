package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.domain.recipe.dto.RecipePageResponseDto;
import com.mars.NangPaGo.domain.recipe.service.RecipeFavoriteService;
import com.mars.NangPaGo.domain.recipe.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/recipe")
@RestController
public class RecipeController {
    private final RecipeFavoriteService recipeFavoriteService;

    private final RecipeLikeService recipeLikeService;

    @PostMapping("/toggle/like")
    public ResponseEntity<String> toggleRecipeLike(@RequestParam Long recipeId, Principal principal) {
        String email = principal.getName();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("유저 인증 실패");
        }

        recipeLikeService.toggleRecipeLike(recipeId, email);
        return ResponseEntity.ok("좋아요 정보를 수정했습니다.");
    }

    @PostMapping("/toggle/favorite")
    public ResponseEntity<String> favoriteRecipe(@RequestParam Long recipeId, Principal principal) throws UserPrincipalNotFoundException {
        String email = Optional.ofNullable(principal)
                .map(Principal::getName)
                .orElseThrow(() -> new UserPrincipalNotFoundException("유저 정보 확인 실패"));

        recipeFavoriteService.toggleRecipeFavorite(email, recipeId);
        return ResponseEntity.ok("레시피 즐겨찾기 토글 변경");
    }

    @GetMapping("/favorite/recipes")
    public ResponseEntity<RecipePageResponseDto> findFavoriteRecipe(
            @RequestParam(defaultValue = "1") int page, Principal principal) throws UserPrincipalNotFoundException {
        String email = Optional.ofNullable(principal)
                .map(Principal::getName)
                .orElseThrow(() -> new UserPrincipalNotFoundException("유저 정보 확인 실패"));

        return ResponseEntity.ok(recipeFavoriteService.findSortedFavoriteRecipes(email, page));
    }
}
