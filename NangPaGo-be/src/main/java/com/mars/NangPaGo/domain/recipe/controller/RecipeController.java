package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeResponseDto;
import com.mars.NangPaGo.domain.recipe.service.RecipeEsService;
import com.mars.NangPaGo.domain.recipe.service.RecipeEsSynchronizerService;
import com.mars.NangPaGo.domain.recipe.service.RecipeLikeService;
import com.mars.NangPaGo.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Tag(name = "레시피 API", description = "레시피 관련 API")
@RequestMapping("/api/recipe")
@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeLikeService recipeLikeService;
    private final RecipeEsService recipeEsService;
    private final RecipeEsSynchronizerService recipeEsSynchronizerService;

    @GetMapping("/{id}")
    public ResponseDto<RecipeResponseDto> recipeById(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeService.recipeById(id), "레시피를 성공적으로 조회했습니다.");
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<Boolean> checkLikeStatus(@PathVariable("id") Long id) {
        return ResponseEntity.ok(recipeLikeService.isLikedByRecipe(id));
    }

    @PostMapping("/{id}/like/toggle")
    public ResponseDto<RecipeLikeResponseDto> toggleLike(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeLikeService.toggleLike(id), "좋아요 이벤트 발생");
    }

    @GetMapping("/search")
    public ResponseDto<Page<RecipeEsResponseDto>> searchRecipes(
        @RequestParam(name = "pageNo", defaultValue = "1") int page,
        @RequestParam(name = "pageSize", defaultValue = "10") int size,
        @RequestParam(name = "keyword", required = false) String keyword) {
        return ResponseDto.of(recipeEsService.searchRecipes(page, size, keyword), "검색 결과를 성공적으로 조회했습니다.");
    }

    @PostMapping("/es/sync")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(recipeEsSynchronizerService.insertRecipeFromMysql(), "MySQL 데이터를 Elastic에 성공적으로 동기화했습니다");
    }
}
