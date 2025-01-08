import RecipeCard from './RecipeCard';
import { useEffect, useState, useRef } from 'react';
import {
  fetchRecommendedRecipes,
  fetchFavoriteRecipes,
} from '../../api/recipe';

function RecipeListContent({ activeTab, searchTerm = '' }) {
  const [recipes, setRecipes] = useState({
    recommended: [],
    favorites: [],
  });
  const [currentPage, setCurrentPage] = useState({
    recommended: 1,
    favorites: 0,
  });
  const [hasMoreRecipes, setHasMoreRecipes] = useState({
    recommended: true,
    favorites: true,
  });
  const observerRef = useRef(null);
  const isFetchingRef = useRef(false);
  const pageSize = 10;

  useEffect(() => {
    const loadInitialRecipes = async () => {
      if (isFetchingRef.current || recipes[activeTab]?.length > 0) return;

      isFetchingRef.current = true;

      try {
        if (activeTab === 'recommended') {
          const data = await fetchRecommendedRecipes(searchTerm, 1, pageSize);
          setRecipes((prev) => ({
            ...prev,
            recommended: data,
          }));
          setHasMoreRecipes((prev) => ({
            ...prev,
            recommended: !data.last,
          }));
          setCurrentPage((prev) => ({
            ...prev,
            recommended: 1,
          }));
        } else if (activeTab === 'favorites') {
          const data = await fetchFavoriteRecipes(0, pageSize);
          setRecipes((prev) => ({
            ...prev,
            favorites: data.content,
          }));
          setHasMoreRecipes((prev) => ({
            ...prev,
            favorites: data.content.length === pageSize,
          }));
          setCurrentPage((prev) => ({
            ...prev,
            favorites: 0,
          }));
        }
      } catch (error) {
        console.error('초기 데이터를 가져오는 중 오류가 발생했습니다:', error);
      } finally {
        isFetchingRef.current = false;
      }
    };

    loadInitialRecipes();
  }, [activeTab, searchTerm]);

  useEffect(() => {
    if (
      (activeTab === 'favorites' && hasMoreRecipes.favorites) ||
      (activeTab === 'recommended' && hasMoreRecipes.recommended)
    ) {
      const observer = new IntersectionObserver(handleObserver, {
        threshold: 1.0,
      });
      if (observerRef.current) observer.observe(observerRef.current);

      return () => observer.disconnect();
    }
  }, [activeTab, hasMoreRecipes]);

  const loadMoreRecipes = async () => {
    if (!hasMoreRecipes[activeTab] || isFetchingRef.current) return;

    isFetchingRef.current = true;

    const nextPage = currentPage[activeTab] + 1;

    try {
      if (activeTab === 'recommended') {
        const data = await fetchRecommendedRecipes(
          searchTerm,
          nextPage,
          pageSize,
        );
        setRecipes((prev) => ({
          ...prev,
          recommended: [...prev.recommended, ...data],
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          recommended: !data.last,
        }));
        setCurrentPage((prev) => ({
          ...prev,
          recommended: nextPage,
        }));
      } else if (activeTab === 'favorites') {
        const data = await fetchFavoriteRecipes(nextPage, pageSize);
        setRecipes((prev) => ({
          ...prev,
          favorites: [
            ...prev.favorites,
            ...data.content.filter(
              (newRecipe) =>
                !prev.favorites.some(
                  (existingRecipe) => existingRecipe.id === newRecipe.id,
                ),
            ),
          ],
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          favorites: data.content.length === pageSize,
        }));
        setCurrentPage((prev) => ({
          ...prev,
          favorites: nextPage,
        }));
      }
    } catch (error) {
      console.error('추가 데이터를 가져오는 중 오류가 발생했습니다:', error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  const handleObserver = ([entry]) => {
    if (entry.isIntersecting) {
      loadMoreRecipes();
    }
  };

  return (
    <div className="grid grid-cols-1 gap-6 min-h-[400px]">
      {recipes[activeTab]?.length > 0 ? (
        recipes[activeTab].map((recipe) => (
          <RecipeCard key={recipe.id} recipe={recipe} />
        ))
      ) : (
        <div className="text-center py-8 text-gray-500">
          {activeTab === 'recommended'
            ? '검색 결과가 없습니다.'
            : '즐겨찾기한 레시피가 없습니다.'}
        </div>
      )}
      <div ref={observerRef} />
    </div>
  );
}

export default RecipeListContent;
