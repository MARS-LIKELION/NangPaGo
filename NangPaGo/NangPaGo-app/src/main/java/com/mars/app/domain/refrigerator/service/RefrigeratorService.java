package com.mars.app.domain.refrigerator.service;

import static com.mars.app.common.exception.NPGExceptionType.DUPLICATE_INGREDIENT;
import static com.mars.app.common.exception.NPGExceptionType.NOT_FOUND_INGREDIENT;
import static com.mars.app.common.exception.NPGExceptionType.UNAUTHORIZED;

import com.mars.app.domain.ingredient.entity.Ingredient;
import com.mars.app.domain.ingredient.repository.IngredientRepository;
import com.mars.app.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.app.domain.refrigerator.entity.Refrigerator;
import com.mars.app.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RefrigeratorService {

    private final RefrigeratorRepository refrigeratorRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public List<RefrigeratorResponseDto> findRefrigerator(String email) {
        return refrigeratorRepository.findByUserEmail(email)
            .stream()
            .map(RefrigeratorResponseDto::from)
            .toList();
    }

    @Transactional
    public void addIngredient(String email, String ingredientName) {
        User user = getUserByEmail(email);
        Ingredient ingredient = getIngredientByName(ingredientName);
        checkIngredientDuplicate(user, ingredient);
        saveIngredientToRefrigerator(user, ingredient);
    }

    @Transactional
    public void deleteIngredient(String email, String ingredientName) {
        refrigeratorRepository.deleteByUserEmailAndIngredientName(email, ingredientName);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> UNAUTHORIZED.of());
    }

    private Ingredient getIngredientByName(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
            .orElseThrow(() -> NOT_FOUND_INGREDIENT.of());
    }

    private void checkIngredientDuplicate(User user, Ingredient ingredient) {
        if (refrigeratorRepository.existsByUserAndIngredient(user, ingredient)) {
            throw DUPLICATE_INGREDIENT.of("이미 냉장고에 동일한 재료가 있습니다.");
        }
    }

    private void saveIngredientToRefrigerator(User user, Ingredient ingredient) {
        refrigeratorRepository.save(Refrigerator.of(user, ingredient));
    }
}
