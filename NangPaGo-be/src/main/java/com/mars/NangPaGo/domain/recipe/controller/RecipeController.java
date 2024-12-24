package com.mars.NangPaGo.domain.recipe.controller;

import com.mars.NangPaGo.domain.recipelike.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> likeRecipe(@RequestParam Long recipeId, Principal principal){
        recipeLikeService.likeRecipe(principal, recipeId);
        return ResponseEntity.ok("레시피를 좋아요 정보를 수정했습니다.");
    }
}
