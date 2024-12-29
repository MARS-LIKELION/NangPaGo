package com.mars.NangPaGo.domain.ingredient.controller;

import com.mars.NangPaGo.domain.ingredient.entity.IngredientElastic;
import com.mars.NangPaGo.domain.ingredient.service.IngredientElasticSearchService;
import com.mars.NangPaGo.domain.ingredient.service.IngredientElasticSynchronizer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/ingredient")
@RestController
public class IngredientElasticController {
    private final IngredientElasticSynchronizer ingredientElasticSynchronizer;
    private final IngredientElasticSearchService ingredientElasticSearchService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncMysql() {
        String response = ingredientElasticSynchronizer.insertIngredientFromMysql();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        String response = ingredientElasticSynchronizer.insertIngredientFromCsv(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    ResponseEntity<List<IngredientElastic>> searchByPrefix(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(ingredientElasticSearchService.searchByPrefix(keyword));
    }
}
