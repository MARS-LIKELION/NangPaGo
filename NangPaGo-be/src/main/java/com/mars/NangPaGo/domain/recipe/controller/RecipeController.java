package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.domain.recipe.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/recipe")
@RestController
public class RecipeController {

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
}
