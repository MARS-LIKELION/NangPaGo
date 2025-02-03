package com.mars.app.domain.userRecipe.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record UserRecipeRequestDto(
    @Size(max = 100, message = "게시물 제목은 최대 100자까지 입력할 수 있습니다.")
    String title,
    String content,
    boolean isPublic,
    List<String> ingredients,
    List<String> manuals
) {
}
