import { useEffect, useState, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  getRefrigerator,
  addIngredient,
  deleteIngredient,
} from '../api/refrigerator';
import { getRecipes } from '../api/recipe';

export function useRefrigerator(recipeSize = 10) {
  const [ingredients, setIngredients] = useState([]);
  const [recipes, setRecipes] = useState(
    () => JSON.parse(localStorage.getItem('recipes')) || [],
  );
  const [recipePage, setRecipePage] = useState(
    () => parseInt(localStorage.getItem('recipePage'), 10) || 1,
  );
  const [hasMoreRecipes, setHasMoreRecipes] = useState(
    () => localStorage.getItem('hasMoreRecipes') === 'true',
  );
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const observerRef = useRef(null);
  const isMounted = useRef(false);

  useEffect(() => {
    fetchRefrigerator();
  }, []);

  useEffect(() => {
    if (!isMounted.current) {
      isMounted.current = true;
      return;
    }
    if (location.pathname === '/refrigerator/recipe') {
      reFetchRefrigeratorRecipes();
    }
  }, [location.pathname, ingredients]);

  useEffect(() => {
    syncLocalStorage();
  }, [recipes, recipePage, hasMoreRecipes]);

  const syncLocalStorage = () => {
    localStorage.setItem('recipes', JSON.stringify(recipes));
    localStorage.setItem('recipePage', recipePage.toString());
    localStorage.setItem('hasMoreRecipes', hasMoreRecipes.toString());
  };

  const handleApiError = (message, error) => {
    console.error(message, error);
  };

  const fetchRefrigerator = async () => {
    try {
      const data = await getRefrigerator();
      setIngredients(data.map((item) => ({ ...item, checked: false })));
    } catch (error) {
      handleApiError('Failed to fetch refrigerator data.', error);
      setIngredients([]);
    }
  };

  const reFetchRefrigeratorRecipes = async () => {
    setRecipePage(1);
    setHasMoreRecipes(true);
    try {
      const ingredientNames = ingredients
        .map((i) => i.ingredientName)
        .filter(Boolean);
      const recipeData = await getRecipes(ingredientNames, 1, recipeSize);
      setRecipes(recipeData.content);
      setHasMoreRecipes(!recipeData.last);
    } catch (error) {
      handleApiError('Error fetching recipes.', error);
    }
  };

  const handleAddIngredient = async (ingredientName) => {
    try {
      const addedIngredient = await addIngredient(ingredientName);
      setIngredients((prev) => [
        ...prev,
        { ...addedIngredient, checked: false },
      ]);
    } catch (error) {
      handleApiError('Error adding ingredient.', error);
    }
  };

  const handleDeleteIngredient = async (ingredientName) => {
    try {
      await deleteIngredient(ingredientName);
      setIngredients((prev) =>
        prev.filter((item) => item.ingredientName !== ingredientName),
      );
    } catch (error) {
      handleApiError('Error deleting ingredient.', error);
    }
  };

  const handleToggleChecked = (name) => {
    setIngredients((prev) =>
      prev.map((item) =>
        item.ingredientName === name
          ? { ...item, checked: !item.checked }
          : item,
      ),
    );
  };

  const handleSelectAll = () => {
    setIngredients((prev) => prev.map((item) => ({ ...item, checked: true })));
  };

  const handleDeselectAll = () => {
    setIngredients((prev) => prev.map((item) => ({ ...item, checked: false })));
  };

  const handleFindRecipes = async () => {
    const checkedItems = ingredients.filter((i) => i.checked);
    if (checkedItems.length === 0) return;

    setRecipePage(1);
    setHasMoreRecipes(true);
    try {
      const ingredientNames = checkedItems.map((i) => i.ingredientName);
      const recipeData = await getRecipes(ingredientNames, 1, recipeSize);
      setRecipes(recipeData.content);
      setHasMoreRecipes(!recipeData.last);
      navigate('/refrigerator/recipe');
    } catch (error) {
      handleApiError('Error finding recipes.', error);
    }
  };

  const loadMoreRecipes = async () => {
    if (!hasMoreRecipes || isLoading) return;
    setIsLoading(true);
    const nextPage = recipePage + 1;
    try {
      const checkedItems = ingredients.filter((i) => i.checked);
      const ingredientNames = checkedItems.map((i) => i.ingredientName);
      const recipeData = await getRecipes(
        ingredientNames,
        nextPage,
        recipeSize,
      );
      setRecipes((prev) => [...prev, ...recipeData.content]);
      setRecipePage(nextPage);
      setHasMoreRecipes(!recipeData.last);
    } catch (error) {
      handleApiError('Error loading more recipes.', error);
    } finally {
      setIsLoading(false);
    }
  };

  const resetAndGoBack = () => {
    setRecipes([]);
    setRecipePage(1);
    setHasMoreRecipes(true);
    localStorage.removeItem('recipes');
    localStorage.removeItem('recipePage');
    localStorage.removeItem('hasMoreRecipes');
    navigate('/refrigerator');
  };

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
