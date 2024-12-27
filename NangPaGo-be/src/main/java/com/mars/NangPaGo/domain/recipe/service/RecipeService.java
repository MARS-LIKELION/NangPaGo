package com.mars.NangPaGo.domain.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;

import com.mars.NangPaGo.domain.recipe.dto.RecipeResponseDto;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeResponseDto recipeById(Long id) {
        return RecipeResponseDto.from(recipeRepository.findById(id)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다.")));
    }
}
