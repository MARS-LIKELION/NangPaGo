package com.mars.NangPaGo.domain.favorite.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;

import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.service.RecipeFavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseDto<List<RecipeFavoriteListResponseDto>> getFavoriteRecipes(@RequestParam("email") String email) {
        return ResponseDto.of(recipeFavoriteService.getFavoriteRecipes(email), "즐겨찾기 레시피를 성공적으로 조회했습니다.");
    }
}
