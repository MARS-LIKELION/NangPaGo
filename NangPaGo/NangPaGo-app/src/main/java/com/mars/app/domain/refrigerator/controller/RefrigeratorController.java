package com.mars.app.domain.refrigerator.controller;

import com.mars.app.common.dto.ResponseDto;
import com.mars.app.common.aop.auth.AuthenticatedUser;
import com.mars.app.common.component.auth.AuthenticationHolder;
import com.mars.app.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.app.domain.refrigerator.service.RefrigeratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "내 냉장고 API", description = "내 냉장고 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/refrigerator")
@RestController
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    @AuthenticatedUser
    @GetMapping
    public ResponseDto<List<RefrigeratorResponseDto>> getRefrigerator() {
        return ResponseDto.of(refrigeratorService.findRefrigerator(AuthenticationHolder.getCurrentUserEmail()));
    }

    @AuthenticatedUser
    @PostMapping
    public ResponseDto<RefrigeratorResponseDto> addIngredient(@RequestParam(name = "ingredientName") String ingredientName) {
        refrigeratorService.addIngredient(AuthenticationHolder.getCurrentUserEmail(), ingredientName);
        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }

    @AuthenticatedUser
    @DeleteMapping
    public ResponseDto<RefrigeratorResponseDto> deleteMyIngredient(@RequestParam(name = "ingredientName") String ingredientName) {
        refrigeratorService.deleteIngredient(AuthenticationHolder.getCurrentUserEmail(), ingredientName);
        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }
}
