import RecipeCard from './RecipeCard';
import { useEffect, useState } from 'react';
import {
  fetchRecommendedRecipes,
  fetchFavoriteRecipes,
} from '../../api/recipe';

function RecipeListContent({ activeTab, searchTerm = '' }) {
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(false); // 로딩 상태 추가
  const pageNo = 1;
  const pageSize = 10;

  useEffect(() => {
    const loadRecipes = async () => {
      setLoading(true); // 로딩 시작

      try {
        if (activeTab === 'recommended') {
          const data = await fetchRecommendedRecipes(
            searchTerm,
            pageNo,
            pageSize,
          );
          setRecipes(data);
        } else if (activeTab === 'favorites') {
          const data = await fetchFavoriteRecipes();

          setRecipes(data);
        }
      } finally {
        setLoading(false); // 로딩 종료
      }
    };

    loadRecipes();
  }, [activeTab, searchTerm]);

  return (
    <div className="grid grid-cols-1 gap-6 min-h-[400px]">
      {loading ? (
        <div className="text-center py-8 text-gray-500">로딩 중...</div>
      ) : recipes.length > 0 ? (
        recipes.map((recipe) => <RecipeCard key={recipe.id} recipe={recipe} />)
      ) : (
        <div className="text-center py-8 text-gray-500">
          {activeTab === 'recommended'
            ? '검색 결과가 없습니다.'
            : '즐겨찾기한 레시피가 없습니다.'}
        </div>
      )}
    </div>
  );
}

export default RecipeListContent;
