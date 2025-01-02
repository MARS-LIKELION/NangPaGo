package com.mars.NangPaGo.domain.ingredient.service;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.ingredient.entity.IngredientEs;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientEsRepository;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class IngredientEsSynchronizer {
    private final IngredientRepository ingredientRepository;
    private final IngredientEsRepository ingredientEsRepository;

    @Transactional
    public String insertIngredientFromMysql() {
        try {
            List<Ingredient> ingredientList = ingredientRepository.findAll();

            List<IngredientEs> ingredientEsList = new ArrayList<>();
            for (Ingredient ingredient : ingredientList) {
                IngredientEs ingredientEs = IngredientEs.of(ingredient.getId(), ingredient.getName());

                ingredientEsList.add(ingredientEs);
            }
            ingredientEsRepository.saveAll(ingredientEsList); // 덮어쓰기
            return "MySQL로부터 데이터를 성공적으로 삽입했습니다!";
        } catch (Exception e) {
            e.printStackTrace();
            return "MySQL 데이터 삽입 중 오류 발생: " + e.getMessage();
        }
    }

    @Transactional
    public String insertIngredientFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            List<IngredientEs> ingredientEsList = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                String id = record.get("id").trim();
                String name = record.get("name").trim();

                IngredientEs ingredientEs = IngredientEs.of(id, name);

                ingredientEsList.add(ingredientEs);
            }
            ingredientEsRepository.saveAll(ingredientEsList);  // 덮어쓰기
            return "CSV 파일로부터 데이터를 성공적으로 삽입했습니다!";
        } catch (Exception e) {
            e.printStackTrace();
            return "CSV 데이터 삽입 중 오류 발생: " + e.getMessage();
        }
    }
}
