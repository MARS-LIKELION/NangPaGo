package com.mars.app.domain.refrigerator.repository;

import com.mars.app.domain.ingredient.repository.IngredientRepository;
import com.mars.common.model.refrigerator.Refrigerator;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.AbstractRepositoryTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
