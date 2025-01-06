package com.mars.NangPaGo.domain.refrigerator.service;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RefrigeratorServiceTest extends IntegrationTestSupport {

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RefrigeratorService refrigeratorService;

    @AfterEach
    void tearDown() {
        refrigeratorRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        ingredientRepository.deleteAllInBatch();
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }

    private Ingredient createIngredient(Long ingredientId, String name) {
        return Ingredient.builder()
            .ingredientId(ingredientId)
            .name(name)
            .build();
    }

    private Refrigerator createRefrigerator(User user, Ingredient ingredient) {
        return Refrigerator.builder()
            .user(user)
            .ingredient(ingredient)
            .build();
    }

    @Transactional
    @DisplayName("사용자의 등록된 냉장고 속 재료 조회")
    @Test
    void findRefrigerator() {
        // given
        String email = "email@example.com";
        User user = createUser(email);

        Ingredient ingredient1 = createIngredient(1L, "당근");
        Ingredient ingredient2 = createIngredient(2L, "양파");

        Refrigerator refrigerator1 = createRefrigerator(user, ingredient1);
        Refrigerator refrigerator2 = createRefrigerator(user, ingredient2);

        userRepository.save(user);
        ingredientRepository.saveAll(List.of(ingredient1, ingredient2));
        refrigeratorRepository.saveAll(List.of(refrigerator1, refrigerator2));

        // when
        List<RefrigeratorResponseDto> result = refrigeratorService.findRefrigerator(email);

        // then
        assertThat(result).hasSize(2)
            .extracting("ingredientName")
            .containsExactlyInAnyOrder("당근", "양파");

    }
}
