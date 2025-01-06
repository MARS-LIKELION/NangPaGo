package com.mars.NangPaGo.domain.ingredient.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.ingredient.dto.IngredientEsResponseDto;
import com.mars.NangPaGo.domain.ingredient.service.IngredientEsService;
import com.mars.NangPaGo.domain.ingredient.service.IngredientEsSynchronizer;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재료사전 API", description = "재료사전 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/ingredient")
@RestController
public class IngredientEsController {

    private final IngredientEsSynchronizer ingredientEsSynchronizer;
    private final IngredientEsService ingredientEsService;

    @PostMapping("/es/bulk-upload/mysql")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(ingredientEsSynchronizer.insertIngredientFromMysql());
    }

    @GetMapping("/search")
    public ResponseDto<List<IngredientEsResponseDto>> searchIngredient(
        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseDto.of(ingredientEsService.searchIngredients(keyword));
    }
}
