package com.mars.NangPaGo.domain.refrigerator.service;

import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.ingredient.repository.IngredientRepository;
import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefrigeratorService {
    private final RefrigeratorRepository refrigeratorRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public List<RefrigeratorResponseDto> findMyRefrigerator(String email) {
        return refrigeratorRepository.findByUserEmail(email)
                .stream().map(RefrigeratorResponseDto::toDto).toList();
    }

    @Transactional
    public void deleteMyIngredient(String email, String ingredientName) {
        refrigeratorRepository.deleteByUser_EmailAndIngredient_Name(ingredientName, email);
    }

    @Transactional
    public void addMyIngredient(String email, String ingredientName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.NOT_FOUND_INGREDIENT));

        try {
            refrigeratorRepository.save(Refrigerator.of(user, ingredient));
        } catch (ConstraintViolationException e) {
            //복합 유니크 제약조건으로 인한 충돌, 추후에 수량 정보를 추가하면 에러 반환 대신 수량을 늘려주는식으로 수정 필요
            throw new NPGException(NPGExceptionType.DUPLICATE_INGREDIENT, "이미 냉장고에 동일한 재료가 있습니다.");
        }
    }
}
