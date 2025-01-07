import RecipeCard from './RecipeCard';
import { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosInstance.js';

function RecipeListContent({ activeTab, searchTerm = '' }) {
  const [recipes, setRecipes] = useState([]); 
  const [hasFetched, setHasFetched] = useState(false);
  const pageNo = 1;
  const pageSize = 10;

  useEffect(() => {
    if (activeTab === 'recommended') {
      fetchRecipes();
      setHasFetched(true);
    }
  }, [activeTab, searchTerm]);

  const fetchRecipes = async () => {
    try {
      const params = {
        pageNo,
        pageSize,
      };

      if (searchTerm) {
        params.keyword = searchTerm;
        params.searchType = 'NAME';
      }

      const response = await axiosInstance.get('/api/recipe/search', { params });
      setRecipes(response.data.data.content);
    } catch (error) {
      console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    }
  };

  return (
    <div className="grid grid-cols-1 gap-6 min-h-[400px]">
      {activeTab === 'recommended' ? (
        recipes.length > 0 ? (
          recipes.map((recipe) => (
            <RecipeCard key={recipe.id} recipe={recipe} />
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            검색 결과가 없습니다.
          </div>
        )
      ) : (
        <div className="text-center py-8 text-gray-500">
          즐겨찾기한 레시피가 없습니다.
        </div>
      )}
    </div>
  );
}

export default RecipeListContent;
