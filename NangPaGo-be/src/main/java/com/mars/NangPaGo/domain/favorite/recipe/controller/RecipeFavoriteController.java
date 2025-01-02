package com.mars.NangPaGo.domain.favorite.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.service.RecipeFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recipe")
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
}
