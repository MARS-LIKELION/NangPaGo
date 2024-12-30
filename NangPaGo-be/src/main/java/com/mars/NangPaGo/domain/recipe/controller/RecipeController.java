package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeResponseDto;
import com.mars.NangPaGo.domain.recipe.service.RecipeEsService;
import com.mars.NangPaGo.domain.recipe.service.RecipeLikeService;
import com.mars.NangPaGo.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequiredArgsConstructor
@Tag(name = "레시피 API", description = "레시피 관련 API")
@RequestMapping("/api/recipe")
@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeLikeService recipeLikeService;
    private final RecipeEsService recipeEsService;

    @GetMapping("/{id}")
    public ResponseDto<RecipeResponseDto> recipeById(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeService.recipeById(id), "레시피를 성공적으로 조회했습니다.");
    }

    @PostMapping("/toggle/like")
    public ResponseEntity<String> toggleRecipeLike(@RequestParam("recipeId") Long recipeId, Principal principal) {
        String email = principal.getName();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("유저 인증 실패");
        }

        recipeLikeService.toggleRecipeLike(recipeId, email);
        return ResponseEntity.ok("좋아요 정보를 수정했습니다.");
    }

    // CSV 파일 업로드로 엘라스틱 데이터 삽입
    @GetMapping("/search")
    public ResponseDto<Page<RecipeEsResponseDto>> searchRecipes(
        @RequestParam(name = "pageNo", defaultValue = "1") int page,
        @RequestParam(name = "pageSize", defaultValue = "10") int size,
        @RequestParam(name = "keyword", required = false) String keyword) {
        Page<RecipeEsResponseDto> results = recipeEsService.searchRecipes(page, size, keyword);
        return ResponseDto.of(results, "검색 결과를 성공적으로 조회했습니다.");
    }

    @PostMapping("/es")
    public ResponseDto<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        String response = recipeEsService.insertRecipesFromCsv(file);
        return ResponseDto.of(response, "CSV 파일 업로드 성공");
    }

    // 엘라스틱 인덱스 생성 API
    @PostMapping("/es/index")
    public ResponseDto<String> createIndex() {
        String response = recipeEsService.createIndex();
        return ResponseDto.of(response, "인덱스를 성공적으로 생성했습니다.");
    }
}
