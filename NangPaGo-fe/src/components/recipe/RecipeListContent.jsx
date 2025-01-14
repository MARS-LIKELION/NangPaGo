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
  const observerInstance = useRef(null);
  const pageSize = 10;
  const isFetching = useRef(false);

  const fetchFunctions = {
    recommended: (page) => fetchRecommendedRecipes(searchTerm, page, pageSize),
    favorites: (page) => fetchFavoriteRecipes(page, pageSize),
  };

  const loadRecipes = async (type, page = 1, isInitial = false) => {
    if (isFetching.current || !hasMoreRecipes[type]) return;

    isFetching.current = true;
    try {
      const { content, last } = await fetchFunctions[type](page);

      setRecipes((prev) => ({
        ...prev,
        [type]: isInitial ? content : [...prev[type], ...content],
      }));
      setHasMoreRecipes((prev) => ({ ...prev, [type]: !last }));
      setCurrentPage((prev) => ({ ...prev, [type]: page }));
    } catch (error) {
      console.error(`Error loading ${type} recipes:`, error);
    } finally {
      isFetching.current = false;
    }
  };

  useEffect(() => {
    if (activeTab === 'favorites' && !isLoggedIn) {
      setRecipes((prev) => ({ ...prev, favorites: [] }));
      setHasMoreRecipes((prev) => ({ ...prev, favorites: false }));
    } else {
      loadRecipes(activeTab, 1, true);
    }
  }, [activeTab, searchTerm, isLoggedIn]);

  useEffect(() => {
    if (observerInstance.current) {
      observerInstance.current.disconnect();
    }

    if (hasMoreRecipes[activeTab]) {
      observerInstance.current = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting) {
            loadRecipes(activeTab, currentPage[activeTab] + 1);
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
  }, [activeTab, hasMoreRecipes, currentPage]);

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
