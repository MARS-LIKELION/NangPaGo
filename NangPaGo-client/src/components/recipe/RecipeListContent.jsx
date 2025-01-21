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
  const pageSize = 12;
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
    <div className="grid sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {recipes[activeTab]?.length > 0 ? (
        recipes[activeTab].map((recipe) => (
          <RecipeCard key={recipe.id} recipe={recipe} />
        ))
      ) : (
        <div className="flex items-center justify-center col-span-full text-text-900">
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
