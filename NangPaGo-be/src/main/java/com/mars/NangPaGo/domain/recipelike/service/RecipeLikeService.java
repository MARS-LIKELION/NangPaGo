package com.mars.NangPaGo.domain.recipelike.service;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.recipelike.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipelike.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeLikeService {
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    private User findUser(Principal principal){
        String userEmail = principal.getName();
        return userRepository.findByEmail(userEmail).get();
    }

    public void likeRecipe(Principal principal,Long recipeId){
        User user = findUser(principal);
        Recipe recipe = recipeRepository.findById(recipeId).get();
        System.out.println(recipe.getClass());
        Optional<RecipeLike> recipeLike = recipeLikeRepository.findByUserAndRecipe(user,recipe);

        if(recipeLike.isEmpty()){
            recipeLikeRepository.save(RecipeLike.of(user,recipe));
        }else{
            recipeLikeRepository.delete(recipeLike.get());
        }
    }
}
