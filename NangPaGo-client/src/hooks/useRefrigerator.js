import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
// refrigerator.js에서 가져오는 함수들
import {
  addIngredient,
  deleteIngredient,
  getRefrigerator,
  searchPostsByIngredient,
} from '../api/refrigerator';
// fetchRecommendedPosts는 api/recipe.js에서 제공됨
import { fetchRecommendedPosts } from '../api/recipe';

export function useRefrigerator(recipeSize = 12) {
  const [ingredients, setIngredients] = useState(
    () => JSON.parse(localStorage.getItem('ingredients')) || []
  );
  const [recipes, setRecipes] = useState(
    () => JSON.parse(localStorage.getItem('recipes')) || []
  );
  const [recipePage, setRecipePage] = useState(
    () => parseInt(localStorage.getItem('recipePage'), 10) || 1
  );
  const [hasMoreRecipes, setHasMoreRecipes] = useState(
    () => localStorage.getItem('hasMoreRecipes') === 'true'
  );

  const isFetching = useRef(false);
  const navigate = useNavigate();
  const observerRef = useRef(null);
  const observerInstance = useRef(null);

  useEffect(() => {
    localStorage.setItem('ingredients', JSON.stringify(ingredients));
    localStorage.setItem('recipes', JSON.stringify(recipes));
    localStorage.setItem('recipePage', recipePage.toString());
    localStorage.setItem('hasMoreRecipes', hasMoreRecipes.toString());
  }, [ingredients, recipes, recipePage, hasMoreRecipes]);

  useEffect(() => {
    fetchRefrigerator();
  }, []);

  useEffect(() => {
    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasMoreRecipes && !isFetching.current) {
          loadMoreRecipes();
        }
      },
      { threshold: 1.0 }
    );

    if (observerRef.current) observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [recipes.length, hasMoreRecipes]);

  const fetchRefrigerator = useCallback(() => {
    getRefrigerator()
      .then((data) =>
        setIngredients(data.map((item) => ({ ...item, checked: false })))
      )
      .catch(() => setIngredients([]));
  }, []);

  const handleAddIngredient = useCallback((ingredientName) => {
    addIngredient(ingredientName)
      .then((addedIngredient) =>
        setIngredients((prev) => [
          ...prev,
          { ...addedIngredient, checked: false },
        ])
      )
      .catch(() =>
        alert('아이템 추가에 실패했습니다. 다시 시도해주세요.')
      );
  }, []);

  const handleDeleteIngredient = useCallback((ingredientName) => {
    deleteIngredient(ingredientName)
      .then(() =>
        setIngredients((prev) =>
          prev.filter((item) => item.ingredientName !== ingredientName)
        )
      )
      .catch(() =>
        alert('아이템 삭제에 실패했습니다. 다시 시도해주세요.')
      );
  }, []);

  const handleToggleChecked = useCallback((name) => {
    setIngredients((prev) =>
      prev.map((item) =>
        item.ingredientName === name
          ? { ...item, checked: !item.checked }
          : item
      )
    );
  }, []);

  const handleSelectAll = useCallback(() => {
    setIngredients((prev) =>
      prev.map((item) => ({ ...item, checked: true }))
    );
  }, []);

  const handleDeselectAll = useCallback(() => {
    setIngredients((prev) =>
      prev.map((item) => ({ ...item, checked: false }))
    );
  }, []);

  const loadMoreRecipes = useCallback(() => {
    if (!hasMoreRecipes || isFetching.current) return;
    isFetching.current = true;

    const nextPage = recipePage + 1;
    const checkedIngredients = ingredients
      .filter((i) => i.checked)
      .map((i) => i.ingredientName);
    const searchTerm = checkedIngredients.join(' ');

    fetchRecommendedPosts(searchTerm, nextPage, recipeSize)
      .then((recipeData) => {
        setRecipes((prev) => [...prev, ...recipeData.content]);
        setRecipePage(nextPage);
        setHasMoreRecipes(!recipeData.last);
      })
      .catch(() =>
        alert('레시피 더 불러오기에 실패했습니다. 다시 시도해주세요.')
      )
      .finally(() => {
        isFetching.current = false;
      });
  }, [hasMoreRecipes, ingredients, recipePage, recipeSize]);

  const handleFindRecipes = useCallback(() => {
    if (isFetching.current) return;
    isFetching.current = true;

    const checkedItems = ingredients.filter((i) => i.checked);
    if (checkedItems.length === 0) {
      isFetching.current = false;
      return;
    }

    const searchTerm = checkedItems
      .map((i) => i.ingredientName)
      .join(' ');

    setRecipePage(1);
    setHasMoreRecipes(true);

    fetchRecommendedPosts(searchTerm, 1, recipeSize)
      .then((recipeData) => {
        setRecipes(recipeData.content);
        setHasMoreRecipes(!recipeData.last);
        navigate('/refrigerator/recipe');
        setTimeout(loadMoreRecipes, 300);
      })
      .catch(() =>
        alert('레시피 검색에 실패했습니다. 다시 시도해주세요.')
      )
      .finally(() => {
        isFetching.current = false;
      });
  }, [ingredients, recipeSize, navigate, loadMoreRecipes]);

  const resetAndGoBack = useCallback(() => {
    setRecipes([]);
    setRecipePage(1);
    setHasMoreRecipes(true);
    localStorage.removeItem('recipes');
    localStorage.removeItem('recipePage');
    localStorage.removeItem('hasMoreRecipes');
    navigate('/refrigerator');
  }, [navigate]);

  return {
    ingredients,
    recipes,
    hasMoreRecipes,
    observerRef,
    handleAddIngredient,
    handleDeleteIngredient,
    handleToggleChecked,
    handleSelectAll,
    handleDeselectAll,
    handleFindRecipes,
    loadMoreRecipes,
    resetAndGoBack,
  };
}
