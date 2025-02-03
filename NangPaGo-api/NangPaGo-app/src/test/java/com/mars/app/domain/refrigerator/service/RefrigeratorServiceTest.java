package com.mars.app.domain.refrigerator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.common.exception.NPGException;
import com.mars.common.model.ingredient.Ingredient;
import com.mars.app.domain.ingredient.repository.IngredientRepository;
import com.mars.app.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.common.model.refrigerator.Refrigerator;
import com.mars.app.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class RefrigeratorServiceTest extends IntegrationTestSupport {

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RefrigeratorService refrigeratorService;

    @Transactional
    @DisplayName("냉장고 속 재료 전체 리스트를 조회할 수 있다.")
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
        List<RefrigeratorResponseDto> result = refrigeratorService.findRefrigerator(user.getId());

        // then
        assertThat(result).hasSize(2)
            .extracting("ingredientName")
            .containsExactlyInAnyOrder("당근", "양파");

    }

    @DisplayName("냉장고에 재료를 추가할 수 있다.")
    @Test
    void addIngredient() {
        // given
        String ingredientName = "당근";
        User user = createUser("email@example.com");
        Ingredient ingredient = createIngredient(1L, ingredientName);

        userRepository.save(user);
        ingredientRepository.save(ingredient);

        // when
        refrigeratorService.addIngredient(user.getId(), ingredientName);

        // then
        List<Refrigerator> refrigerators = refrigeratorRepository.findByUserId(user.getId());
        assertThat(refrigerators).hasSize(1);
        assertThat(refrigerators.get(0).getIngredient().getName()).isEqualTo(ingredientName);
    }

    @DisplayName("냉장고 속 식재료를 삭제할 수 있다.")
    @Test
    void deleteIngredient() {
        // given
        String ingredientName = "당근";
        User user = createUser("email@example.com");
        Ingredient ingredient = createIngredient(1L, ingredientName);
        Refrigerator refrigerator = createRefrigerator(user, ingredient);

        userRepository.save(user);
        ingredientRepository.save(ingredient);
        refrigeratorRepository.save(refrigerator);

        // when
        refrigeratorService.deleteIngredient(user.getId(), ingredientName);

        // then
        List<Refrigerator> refrigerators = refrigeratorRepository.findByUserId(user.getId());
        assertThat(refrigerators).hasSize(0);
    }

    @DisplayName("중복된 식재료 추가를 시도할 때 예외가 발생한다.")
    @Test
    void checkIngredientDuplicate() {
        // given
        String ingredientName = "당근";
        User user = createUser("email@example.com");
        Ingredient ingredient = createIngredient(1L, ingredientName);
        Refrigerator refrigerator = createRefrigerator(user, ingredient);

        userRepository.save(user);
        ingredientRepository.save(ingredient);
        refrigeratorRepository.save(refrigerator);

        // when & then
        assertThatThrownBy(() -> refrigeratorService.addIngredient(user.getId(), ingredientName))
            .isInstanceOf(NPGException.class)
            .hasMessage("이미 냉장고에 동일한 재료가 있습니다.");
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
}
