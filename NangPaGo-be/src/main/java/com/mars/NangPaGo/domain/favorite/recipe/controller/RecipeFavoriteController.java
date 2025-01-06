package com.mars.NangPaGo.domain.favorite.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;

import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.service.RecipeFavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Tag(name = "레시피 즐겨찾기 API", description = "레시피 즐겨찾기 관련 API")
@RequestMapping("/api/recipe")
@RestController
public class RecipeFavoriteController {

    private final RecipeFavoriteService recipeFavoriteService;

    @GetMapping("/{id}/favorite/status")
    public ResponseEntity<Boolean> isFavorite(@PathVariable("id") Long id) {
        return ResponseEntity.ok(recipeFavoriteService.isFavorite(id));
    }

    @PostMapping("/{id}/favorite/toggle")
    public ResponseDto<RecipeFavoriteResponseDto> toggleFavorite(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeFavoriteService.toggleFavorite(id), "즐겨찾기 이벤트 발생");
    }

    @GetMapping("/favorite/list")
    public ResponseDto<List<RecipeFavoriteListResponseDto>> getFavoriteRecipes(Principal principal) {
        String email = Optional.ofNullable(principal)
            .map(Principal::getName)
            .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED, "사용자 인증 정보가 없습니다."));

        List<RecipeFavoriteListResponseDto> favoriteRecipes = recipeFavoriteService.getFavoriteRecipes(email);
        return ResponseDto.of(favoriteRecipes);
    }
}
