package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.entity.RecipeES;
import com.mars.NangPaGo.domain.recipe.repository.RecipeESRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeEsService {

    private final RecipeESRepository recipeESRepository;

    @Transactional
    public String insertRecipesFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreSurroundingSpaces()
                    .withTrim()
                    .withQuote('"'));

            List<RecipeES> recipeESList = new ArrayList<>();
            int lineNumber = 1;

            for (CSVRecord record : csvParser) {
                try {
                    // CSV 데이터 읽기
                    String id = record.get("RCP_SEQ").trim();
                    String name = record.get("RCP_NM").trim();
                    String ingredientsData = record.get("RCP_PARTS_DTLS").trim().replaceAll("\n", " ");

                    // 빈 필드 검증
                    if (id.isEmpty() || name.isEmpty() || ingredientsData.isEmpty()) {
                        System.err.println("빈 필드가 포함된 행: " + record.toString());
                        continue;
                    }

                    if (ingredientsData.length() > 512) {
                        ingredientsData = ingredientsData.substring(0, 512);
                        System.err.println("Ingredients field truncated at line (" + lineNumber + ")");
                    }

                    RecipeES recipeES = new RecipeES();
                    recipeES.setId(id);
                    recipeES.setName(name);
                    recipeES.setIngredients(List.of(ingredientsData.split(",")).stream()
                            .map(String::trim)
                            .filter(ingredient -> !ingredient.isEmpty())
                            .toList());

                    recipeESList.add(recipeES);
                    lineNumber++;
                } catch (Exception e) {
                    System.err.println("Error parsing line (" + lineNumber + "): " + e.getMessage());
                }
            }
            recipeESRepository.saveAll(recipeESList);
            return "CSV 파일로부터 데이터를 성공적으로 삽입했습니다!";
        } catch (Exception e) {
            e.printStackTrace();
            return "CSV 데이터 삽입 중 오류 발생: " + e.getMessage();
        }
    }
}

