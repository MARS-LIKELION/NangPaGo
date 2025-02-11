package com.mars.app.domain.recipe.controller;

import com.mars.app.aop.visit.VisitLog;
import com.mars.app.domain.recipe.dto.RecipeEsListResponseDto;
import com.mars.app.domain.recipe.dto.RecipeSearchResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.service.RecipeEsService;
import com.mars.app.domain.recipe.service.RecipeEsSynchronizerService;
import com.mars.app.domain.recipe.service.RecipeService;
import com.mars.common.dto.page.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "레시피 API", description = "공공 데이터 레시피 검색 및 추천")
@RequestMapping("/api/recipe")
@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeEsService recipeEsService;
    private final RecipeEsSynchronizerService recipeEsSynchronizerService;

    @Operation(summary = "레시피 상세 조회")
    @GetMapping("/{id}")
    public ResponseDto<RecipeResponseDto> recipeById(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeService.recipeById(id));
    }

    @VisitLog
    @Operation(summary = "추천 레시피 조회 및 검색", description = "keyword 를 비워서 요청하면 추천 레시피 조회 동작")
    @GetMapping("/recommendations")
    public ResponseDto<PageResponseDto<RecipeEsListResponseDto>> searchRecipes(
        PageRequestVO pageRequestVO,
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "searchType", defaultValue = "INGREDIENTS") String searchType) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeEsService.searchRecipes(userId, keyword, searchType, pageRequestVO));
    }

    @Operation(summary = "키워드로 레시피 검색", description = "검색 시 최소한의 정보만 반환")
    @GetMapping("/search")
    public ResponseDto<Page<RecipeSearchResponseDto>> searchRecipesByKeyword(
        PageRequestVO pageRequestVO,
        @RequestParam(name = "keyword") String keyword,
        @RequestParam(name = "searchType", defaultValue = "NAME") String searchType
    ) {
        Page<RecipeSearchResponseDto> results = recipeEsService.searchRecipeByKeyword(pageRequestVO, keyword, searchType);
        return ResponseDto.of(results);
    }

    @Operation(summary = "MySQL 원천 데이터를 ES에 덮어쓰기", description = "ES의 기존 데이터 삭제 후 재생성 (실행 전 주의 필요!!)")
    @PostMapping("/bulk-upload/mysql")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(recipeEsSynchronizerService.insertRecipeFromMysql(), "MySQL 데이터를 Elastic에 성공적으로 동기화했습니다");
    }
}
