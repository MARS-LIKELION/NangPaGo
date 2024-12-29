package com.mars.NangPaGo.domain.ingredient.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.ingredient.entity.IngredientElastic;
import com.mars.NangPaGo.domain.ingredient.service.IngredientElasticSearchService;
import com.mars.NangPaGo.domain.ingredient.service.IngredientElasticSynchronizer;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Tag(name = "재료사전 API", description = "재료사전 관련 API")
@RequestMapping("/ingredient")
@RestController
public class IngredientElasticController {
    private final IngredientElasticSynchronizer ingredientElasticSynchronizer;
    private final IngredientElasticSearchService ingredientElasticSearchService;

    @PostMapping("/sync")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(ingredientElasticSynchronizer.insertIngredientFromMysql());
    }

    @PostMapping("/upload")
    public ResponseDto<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        return ResponseDto.of(ingredientElasticSynchronizer.insertIngredientFromCsv(file));
    }

    @GetMapping("/search")
    public ResponseDto<List<IngredientElastic>> searchByPrefix(@RequestParam("keyword") String keyword) {
        return ResponseDto.of(ingredientElasticSearchService.searchByPrefix(keyword));
    }
}
