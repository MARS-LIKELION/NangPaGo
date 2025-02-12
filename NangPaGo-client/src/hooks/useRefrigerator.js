import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  addIngredient,
  deleteIngredient,
  getRefrigerator,
  searchPostsByIngredient,
} from '../api/refrigerator';
import { fetchRecommendedPosts } from '../api/recipe';

export function useRefrigerator(recipeSize = 12) {
  const [ingredients, setIngredients] = useState([]);
  const [recipes, setRecipes] = useState([]);
  const [recipePage, setRecipePage] = useState(1);
  const [hasMoreRecipes, setHasMoreRecipes] = useState(true);
  const isFetching = useRef(false);
  const navigate = useNavigate();
  const location = useLocation();
  const observerRef = useRef(null);
  const observerInstance = useRef(null);

  const fetchRefrigerator = useCallback(() => {
    getRefrigerator()
      .then((data) => {
        setIngredients(
          data.map((item) => ({ ...item, checked: false }))
        );
      })
      .catch(() => setIngredients([]));
  }, []);

  const loadMoreRecipes = useCallback(() => {
    if (!hasMoreRecipes || isFetching.current) return;
    isFetching.current = true;

    const checkedIngredients = ingredients
      .filter((i) => i.checked)
      .map((i) => i.ingredientName);
    const searchTerm = checkedIngredients.join(' ');

    setRecipePage((currentPage) => {
      const nextPage = currentPage + 1;

      fetchRecommendedPosts(searchTerm, nextPage, recipeSize)
        .then((recipeData) => {
          setRecipes((prev) => [...prev, ...recipeData.content]);
          setHasMoreRecipes(!recipeData.last);
        })
        .catch((error) => {
          console.error('추가 레시피 로딩 실패:', error);
          alert('레시피 더 불러오기에 실패했습니다. 다시 시도해주세요.');
        })
        .finally(() => {
          isFetching.current = false;
        });

      return nextPage;
    });
  }, [hasMoreRecipes, ingredients, recipeSize]);

  const handleAddIngredient = useCallback((ingredientName) => {
    addIngredient(ingredientName)
      .then((addedIngredient) => {
        setIngredients((prev) => [
          ...prev,
          { ...addedIngredient, checked: false },
        ]);
      })
      .catch(() =>
        alert('아이템 추가에 실패했습니다. 다시 시도해주세요.')
      );
  }, []);

  const handleDeleteIngredient = useCallback((ingredientName) => {
    deleteIngredient(ingredientName)
      .then(() => {
        setIngredients((prev) =>
          prev.filter(
            (item) => item.ingredientName !== ingredientName
          )
        );
      })
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

  const handleFindRecipes = useCallback(() => {
    if (isFetching.current) return;
    isFetching.current = true;

    const checkedItems = ingredients.filter((i) => i.checked);
    if (checkedItems.length === 0) {
      isFetching.current = false;
      return;
    }

    const searchIngredients = checkedItems.map(
      (i) => i.ingredientName
    );
    const searchTerm = searchIngredients.join(' ');

    localStorage.setItem(
      'lastSearchIngredients',
      JSON.stringify(searchIngredients)
    );

    setRecipes([]);
    setRecipePage(1);
    setHasMoreRecipes(true);

    fetchRecommendedPosts("INGREDIENTS", searchTerm, 1, recipeSize)
      .then((recipeData) => {
        setRecipes(recipeData.content);
        setHasMoreRecipes(!recipeData.last);
        navigate('/refrigerator/recipe');
      })
      .catch((error) => {
        console.error('레시피 검색 실패:', error);
        alert('레시피 검색에 실패했습니다. 다시 시도해주세요.');
      })
      .finally(() => {
        isFetching.current = false;
      });
  }, [ingredients, recipeSize, navigate]);

  const resetAndGoBack = useCallback(() => {
    setRecipes([]);
    setRecipePage(1);
    setHasMoreRecipes(true);
    localStorage.removeItem('lastSearchIngredients');
    handleDeselectAll();
    navigate('/refrigerator');
  }, [navigate, handleDeselectAll]);


  useEffect(() => {
    if (location.pathname === '/refrigerator/recipe') {
      const lastSearchIngredients = JSON.parse(
        localStorage.getItem('lastSearchIngredients')
      );
      if (lastSearchIngredients && recipes.length === 0) {
        const searchTerm = lastSearchIngredients.join(' ');
        fetchRecommendedPosts("INGREDIENTS", searchTerm, 1, recipeSize)
          .then((recipeData) => {
            setRecipes(recipeData.content);
            setRecipePage(1);
            setHasMoreRecipes(!recipeData.last);
          })
          .catch((error) => {
            console.error('레시피 검색 실패:', error);
          });
      }
    }
  }, [location.pathname, recipes.length, recipeSize]);

  useEffect(() => {
    localStorage.setItem(
      'ingredients',
      JSON.stringify(ingredients)
    );
    localStorage.setItem('recipes', JSON.stringify(recipes));
    localStorage.setItem('recipePage', recipePage.toString());
    localStorage.setItem(
      'hasMoreRecipes',
      hasMoreRecipes.toString()
    );
  }, [ingredients, recipes, recipePage, hasMoreRecipes]);

  useEffect(() => {
    if (location.pathname !== '/refrigerator/recipe') {
      fetchRefrigerator();
    }
  }, [location.pathname, fetchRefrigerator]);

  useEffect(() => {
    if (location.pathname !== '/refrigerator/recipe') {
      if (observerInstance.current) {
        observerInstance.current.disconnect();
      }
      return;
    }

    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (
          entry.isIntersecting &&
          hasMoreRecipes &&
          !isFetching.current
        ) {
          loadMoreRecipes();
        }
      },
      { threshold: 1.0 }
    );

    if (observerRef.current)
      observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current)
        observerInstance.current.disconnect();
    };
  }, [recipes.length, hasMoreRecipes, location.pathname, loadMoreRecipes]);

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
