package com.mars.NangPaGo.domain.refrigerator.repository;

import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class RefrigeratorRepositoryTest {
    @Autowired
    private RefrigeratorRepository refrigeratorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @DisplayName("사용자의 등록된 냉장고 속 재료를 삭제")
    @Test
    void deleteByUser_EmailAndIngredient_Name() {
        // given
        String email = "dummy@nangpago.com";
        String ingredientName = "가람마살라";

        // when, 임의로 추가한 데이터 삭제
        refrigeratorRepository.deleteByUser_EmailAndIngredient_Name(email, ingredientName);

        // then
        List<Refrigerator> remainingRefrigerators = refrigeratorRepository.findByUserEmail(email);
        assertThat(remainingRefrigerators.size()).isGreaterThan(3); // 테스트 현재 데이터 5개 존재
/*        assertThat(remainingRefrigerators.size()).isGreaterThan(4); // 에러가 떠야 정상, 성공한다면 DB확인 필요*/
    }

    @DisplayName("사용자의 등록된 냉장고 속 재료를 추가")
    @Test
    void addMyIngredient() {
        // given
        String email = "dummy@nangpago.com";
        String ingredientName = "가래떡";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.NOT_FOUND_INGREDIENT));

        // when, 임의로 추가한 데이터 추가
        refrigeratorRepository.save(Refrigerator.of(user, ingredient));

        // then
        List<Refrigerator> remainingRefrigerators = refrigeratorRepository.findByUserEmail(email);
        assertThat(remainingRefrigerators.size()).isGreaterThan(5); // 테스트 현재 데이터 5개 존재
/*        assertThat(remainingRefrigerators.size()).isGreaterThan(6); // 에러가 떠야 정상, 성공한다면 DB확인 필요*/
    }
}