package com.mars.NangPaGo.domain.refrigerator.service;

import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class RefrigeratorServiceTest {
    @Autowired
    private RefrigeratorService refrigeratorService;

    @DisplayName("사용자의 등록된 냉장고 속 재료 조회")
    @Test
    void findMyRefrigerator() {
        // given
        String email = "dummy@nangpago.com";

        // when
        List<RefrigeratorResponseDto> refrigerator = refrigeratorService.findMyRefrigerator(email);

        //then
        assertThat(refrigerator.size()).isGreaterThan(2);
    }
}