package com.mars.NangPaGo.domain.refrigerator.repository;

import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.support.AbstractRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
class RefrigeratorRepositoryTest extends AbstractRepositoryTestSupport {
    @Autowired
    private RefrigeratorRepository refrigeratorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @DisplayName("UserEmail로 Refrigerator 테이블 조회")
    @Test
    void deleteByUser_EmailAndIngredient_Name() {
        // given
        String email = "dummy@nangpago.com";

        // when, 임의로 추가한 데이터 삭제
        List<Refrigerator> remainingRefrigerators = refrigeratorRepository.findByUserEmail(email);

        // then
        if (!remainingRefrigerators.isEmpty()) {
            System.out.println(remainingRefrigerators.size());
        }
    }
}
