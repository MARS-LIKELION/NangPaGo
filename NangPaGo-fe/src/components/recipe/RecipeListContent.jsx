import RecipeCard from './RecipeCard';
import { useEffect, useState, useRef } from 'react';
import {
  fetchRecommendedRecipes,
  fetchFavoriteRecipes,
} from '../../api/recipe';

function RecipeListContent({ activeTab, searchTerm = '', isLoggedIn }) {
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
  const observerInstance = useRef(null);
  const pageSize = 10;

  const loadMoreRecipes = async () => {
    if (!hasMoreRecipes[activeTab] || isFetchingRef.current) return;

    isFetchingRef.current = true;
    const nextPage = currentPage[activeTab] + 1;

    try {
      if (activeTab === 'recommended') {
        const { content, last } = await fetchRecommendedRecipes(
          searchTerm,
          nextPage,
          pageSize,
        );

        setRecipes((prev) => ({
          ...prev,
          recommended: [...prev.recommended, ...content],
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          recommended: !last, // 마지막 페이지 여부 업데이트
        }));
        setCurrentPage((prev) => ({
          ...prev,
          recommended: nextPage,
        }));
      } else if (activeTab === 'favorites') {
        const { content, last } = await fetchFavoriteRecipes(
          nextPage,
          pageSize,
        );

        setRecipes((prev) => ({
          ...prev,
          favorites: [
            ...prev.favorites,
            ...content.filter(
              (newRecipe) =>
                !prev.favorites.some(
                  (existingRecipe) => existingRecipe.id === newRecipe.id,
                ),
            ),
          ],
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          favorites: !last,
        }));
        setCurrentPage((prev) => ({
          ...prev,
          favorites: nextPage,
        }));
      }
    } catch (error) {
      console.error('Error loading more recipes:', error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  const loadInitialRecipes = async () => {
    if (isFetchingRef.current || recipes[activeTab]?.length > 0) return;

    isFetchingRef.current = true;

    try {
      if (activeTab === 'recommended') {
        const { content, last } = await fetchRecommendedRecipes(
          searchTerm,
          1,
          pageSize,
        );

        setRecipes((prev) => ({
          ...prev,
          recommended: content,
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          recommended: !last,
        }));
        setCurrentPage((prev) => ({
          ...prev,
          recommended: 1,
        }));
      } else if (activeTab === 'favorites') {
        const { content, last } = await fetchFavoriteRecipes(0, pageSize);

        setRecipes((prev) => ({
          ...prev,
          favorites: content,
        }));
        setHasMoreRecipes((prev) => ({
          ...prev,
          favorites: !last,
        }));
        setCurrentPage((prev) => ({
          ...prev,
          favorites: 0,
        }));
      }
    } catch (error) {
      console.error('Error loading initial recipes:', error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  useEffect(() => {
    loadInitialRecipes();
  }, [activeTab, searchTerm]);

  useEffect(() => {
    if (observerInstance.current) {
      observerInstance.current.disconnect();
    }

    if (hasMoreRecipes[activeTab]) {
      observerInstance.current = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting) {
            loadMoreRecipes();
          }
        },
        { threshold: 1.0 },
      );

      if (observerRef.current) {
        observerInstance.current.observe(observerRef.current);
      }
    }

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [activeTab, hasMoreRecipes]);

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
            : isLoggedIn
              ? '즐겨찾기한 레시피가 없습니다.'
              : '로그인 후 이용 가능합니다.'}
        </div>
      )}
      <div ref={observerRef} />
    </div>
  );
}

export default RecipeListContent;
