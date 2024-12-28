package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.domain.recipe.dto.RecipeEsDto;
import com.mars.NangPaGo.domain.recipe.service.RecipeEsService;
import com.mars.NangPaGo.domain.recipe.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/recipe")
@RestController
public class RecipeController {

    private final RecipeLikeService recipeLikeService;
    private final RecipeEsService recipeEsService;

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

    //데이터 업로드
    @PostMapping("/es")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        String response = recipeEsService.insertRecipesFromCsv(file);
        return ResponseEntity.ok(response);
    }

    // 검색 API
    @GetMapping("/es")
    public ResponseEntity<Page<RecipeEsDto>> searchRecipes(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<RecipeEsDto> results = recipeEsService.searchRecipes(page, size, keyword);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/es/index")
    public ResponseEntity<String> createIndex() {
        String response = recipeEsService.createIndex();
        return ResponseEntity.ok(response);
    }
}
