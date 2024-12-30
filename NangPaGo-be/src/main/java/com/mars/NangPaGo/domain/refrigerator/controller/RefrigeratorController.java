package com.mars.NangPaGo.domain.refrigerator.controller;

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
@RequestMapping("/api/refrigerator")
@RestController
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    @GetMapping
    public ResponseDto<List<RefrigeratorResponseDto>> getRefrigerator(Principal principal) {
        String email = Optional.ofNullable(principal)
            .map(Principal::getName)
            .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));

        return ResponseDto.of(refrigeratorService.findRefrigerator(email), "내 냉장고를 성공적으로 조회했습니다.");
    }

    @PostMapping
    public ResponseDto<RefrigeratorResponseDto> addIngredient(Principal principal, @RequestParam(name = "ingredientName") String ingredientName) {
        String email = Optional.ofNullable(principal)
            .map(Principal::getName)
            .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));

        refrigeratorService.addIngredient(email, ingredientName);

        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }

    @DeleteMapping
    public ResponseDto<RefrigeratorResponseDto> deleteMyIngredient(Principal principal, @RequestParam(name = "ingredientName") String ingredientName) {
        String email = Optional.ofNullable(principal)
            .map(Principal::getName)
            .orElseThrow(() -> new NPGException(NPGExceptionType.UNAUTHORIZED));

        refrigeratorService.deleteIngredient(email, ingredientName);

        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }
}
