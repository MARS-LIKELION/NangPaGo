package com.mars.NangPaGo.domain.user.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.NangPaGo.domain.refrigerator.service.RefrigeratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final RefrigeratorService refrigeratorService;

    @GetMapping("/refrigerator")
    public ResponseDto<List<RefrigeratorResponseDto>> recipeById(Principal principal) {
        String email = Optional.ofNullable(principal)
                .map(Principal::getName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        return ResponseDto.of(refrigeratorService.findMyRefrigerator(email), "내 냉장고를 성공적으로 조회했습니다.");
    }

    @DeleteMapping("/refrigerator")
    public ResponseDto<String> deleteMyIngredient(Principal principal, @RequestParam(name = "ingredientName") String ingredientName) {
        String email = Optional.ofNullable(principal)
                .map(Principal::getName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        refrigeratorService.deleteMyIngredient(email, ingredientName);
        return ResponseDto.of("", ingredientName + "를 냉장고에서 제외하였습니다.");
    }

    @PostMapping("/refrigerator/addIngredient")
    public ResponseDto<String> addMyIngredient(Principal principal, @RequestParam(name = "ingredientName") String ingredientName) {
        String email = Optional.ofNullable(principal)
                .map(Principal::getName)
                .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));
        refrigeratorService.addMyIngredient(email, ingredientName);
        return ResponseDto.of("", ingredientName + "를 냉장고에 추가하였습니다.");
    }
}
