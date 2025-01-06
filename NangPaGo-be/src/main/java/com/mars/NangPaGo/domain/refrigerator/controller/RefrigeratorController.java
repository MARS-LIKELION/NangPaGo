package com.mars.NangPaGo.domain.refrigerator.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.domain.refrigerator.dto.RefrigeratorResponseDto;
import com.mars.NangPaGo.domain.refrigerator.service.RefrigeratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "내 냉장고 API", description = "내 냉장고 관련 API")
@RequestMapping("/api/refrigerator")
@RestController
public class RefrigeratorController {

    private final RefrigeratorService refrigeratorService;

    @AuthenticatedUser
    @GetMapping
    public ResponseDto<List<RefrigeratorResponseDto>> getRefrigerator() {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(refrigeratorService.findRefrigerator(email), "");
    }

    @AuthenticatedUser
    @PostMapping
    public ResponseDto<RefrigeratorResponseDto> addIngredient(@RequestParam(name = "ingredientName") String ingredientName) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        refrigeratorService.addIngredient(email, ingredientName);

        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }

    @AuthenticatedUser
    @DeleteMapping
    public ResponseDto<RefrigeratorResponseDto> deleteMyIngredient(@RequestParam(name = "ingredientName") String ingredientName) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        refrigeratorService.deleteIngredient(email, ingredientName);

        return ResponseDto.of(RefrigeratorResponseDto.from(ingredientName));
    }
}
