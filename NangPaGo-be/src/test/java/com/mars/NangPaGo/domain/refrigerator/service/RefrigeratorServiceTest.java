package com.mars.NangPaGo.domain.refrigerator.service;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class RefrigeratorServiceTest {

    @Mock
    private RefrigeratorRepository refrigeratorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RefrigeratorService refrigeratorService;

    @DisplayName("사용자의 등록된 냉장고 속 재료 조회")
    @Test
    void findRefrigerator() {
        // given
        String email = "dummy@nangpago.com";
        User user = User.builder()
            .email(email)
            .build();

        Ingredient ingredient1 = Ingredient.builder()
            .name("당근")
            .build();

        Ingredient ingredient2 = Ingredient.builder()
            .name("양파")
            .build();

        List<Refrigerator> expectedResponse = Arrays.asList(
            Refrigerator.of(user, ingredient1),
            Refrigerator.of(user, ingredient2)
        );

        // Mocking
        when(refrigeratorRepository.findByUserEmail(anyString()))
            .thenReturn(expectedResponse);

        // when
        List<RefrigeratorResponseDto> result = refrigeratorService.findRefrigerator(email);

        // then
        assertThat(result).hasSize(2)
            .extracting("ingredientName")
            .containsExactlyInAnyOrder("당근", "양파");
        verify(refrigeratorRepository, times(1)).findByUserEmail(email);
    }
}
