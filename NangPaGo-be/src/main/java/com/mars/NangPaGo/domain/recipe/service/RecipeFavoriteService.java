package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.dto.RecipeListResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeFavoriteService {
    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleRecipeFavorite(String email, Long recipeId) {
        Optional<RecipeFavorite> recipeFavorite = recipeFavoriteRepository.findByEmailAndRecipeId(email, recipeId);

        toggleRecipeFavorite(recipeFavorite, email, recipeId);
    }

    @Transactional
    public Page<RecipeListResponseDto> findSortedFavoritRecipes(String email, int page) {
        Pageable pageable = PageRequest.of(page - 1, 5);

        // 처음 Page로 받은 다음, Page -> List(Dto 전환) -> Page(PageImpl)로 변환하는 과정이 이상하긴한데,
        // 처음에 List로 반환해서 PageImpl을 생성 할 경우 데이터량을 넘는 페이지 수를 접근할 때 getTotalPages와 totalElements를 이상하게 가져오고,
        // PageImpl에 pageable를 다시 넣는 이유도 TotalElements를 읽지 못하기 때문에 이와같이 됨
        Page<Recipe> favoriteRecipes = findFavoriteRecipes(email, pageable);
        long totalElements = favoriteRecipes.getTotalElements();

        if (page < 1 || page > totalElements) {
            throw new NoSuchElementException("요청한 페이지에는 데이터가 없습니다.");
        }
        // dto 변환
        List<RecipeListResponseDto> dtoList = favoriteRecipes.stream().map(RecipeListResponseDto::toDto).toList();

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    private Page<Recipe> findFavoriteRecipes(String email, Pageable pageable) {
        return recipeFavoriteRepository.findByEmail(email, pageable);
    }

    private void toggleRecipeFavorite(Optional<RecipeFavorite> recipeFavorite, String email, Long recipeId) {
        if (recipeFavorite.isEmpty()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("not found user"));
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new EntityNotFoundException("not found recipe"));

            recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
        } else {
            recipeFavoriteRepository.delete(recipeFavorite.get());
        }
    }
}
