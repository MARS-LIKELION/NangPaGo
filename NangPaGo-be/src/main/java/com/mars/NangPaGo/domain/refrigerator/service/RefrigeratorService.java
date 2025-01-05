package com.mars.NangPaGo.domain.refrigerator.service;

import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.component.AuthenticationHolder;
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

    public List<RefrigeratorResponseDto> findRefrigerator(String email) {
        return refrigeratorRepository.findByUserEmail(email)
                .stream().map(RefrigeratorResponseDto::from).toList();
    }

    @Transactional
    public void deleteIngredient(String email, String ingredientName) {
        refrigeratorRepository.deleteByUser_EmailAndIngredient_Name(ingredientName, email);
    }

    @Transactional
    public void addIngredient(String email, String ingredientName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.NOT_FOUND_INGREDIENT));

        try {
            refrigeratorRepository.save(Refrigerator.of(user, ingredient));
        } catch (ConstraintViolationException e) {
            throw new NPGException(NPGExceptionType.DUPLICATE_INGREDIENT, "이미 냉장고에 동일한 재료가 있습니다.");
        }
    }
}
