import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  getRefrigerator,
  addIngredient,
  deleteIngredient,
} from '../../api/refrigerator';
import { getRecipes } from '../../api/recipe.js';
import Header from '../../components/common/Header';
import IngredientList from '../../components/refrigerator/IngredientList';
import AddIngredientForm from '../../components/refrigerator/AddIngredientForm';
import RecipeCard from '../../components/recipe/RecipeCard';

const Refrigerator = () => {
  const [ingredients, setIngredients] = useState([]);
  const [recipes, setRecipes] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    fetchRefrigerator(page, size);
  }, [page, size]);

  const fetchRefrigerator = async (page, size) => {
    try {
      const data = await getRefrigerator(page, size);
      setIngredients(data);
    } catch (error) {
      console.error('냉장고 데이터를 가져오는 데 실패했습니다.', error);
      setIngredients([]);
    }
  };

  const handleAddIngredient = async (ingredientName) => {
    try {
      const addedIngredient = await addIngredient(ingredientName);
      setIngredients((prev) => [...prev, addedIngredient]);
    } catch (error) {
      console.error('재료 추가 중 오류가 발생했습니다.', error);
    }
  };

  const handleDeleteIngredient = async (ingredientName) => {
    try {
      const message = await deleteIngredient(ingredientName);
      console.log(message);
      setIngredients((prev) =>
        prev.filter((item) => item.ingredientName !== ingredientName),
      );
    } catch (error) {
      console.error('재료 삭제 중 오류가 발생했습니다.', error);
    }
  };

  const handleFindRecipes = async () => {
    try {
      const ingredientNames = ingredients
        .filter((i) => i && i.ingredientName)
        .map((i) => i.ingredientName);

      const recipeData = await getRecipes(ingredientNames);
      setRecipes(recipeData);
      navigate('/refrigerator/recipes');
    } catch (error) {
      console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col items-center">
      <Header title="냉장고 파먹기" />

      {location.pathname === '/refrigerator' ? (
        <>
          <div className="w-full px-4 mt-4">
            <AddIngredientForm onAdd={handleAddIngredient} />
          </div>

          <div className="w-full px-4 mt-6 mb-4">
            <h2 className="text-lg font-medium mb-2">내 냉장고</h2>
            <IngredientList
              ingredients={ingredients}
              onDelete={handleDeleteIngredient}
            />
          </div>

          <div className="w-full px-4 mt-auto mb-4">
            <button
              className="bg-[var(--primary-color)] text-white w-full py-3 rounded-lg text-lg font-medium"
              onClick={handleFindRecipes}
            >
              레시피 찾기
            </button>
          </div>
        </>
      ) : (
        <div className="w-full px-4 mt-6">
          <h2 className="text-lg font-medium mb-4">추천 레시피</h2>
          <div className="grid grid-cols-1 gap-6 min-h-[400px]">
            {recipes.length > 0 ? (
              recipes.map((recipe) => (
                <RecipeCard key={recipe.id} recipe={recipe} />
              ))
            ) : (
              <div className="text-center py-8 text-gray-500">
                추천할 레시피가 없습니다.
              </div>
            )}
          </div>
          <button
            className="bg-[var(--primary-color)] text-white w-full py-3 mt-4 mb-4 rounded-lg text-lg font-medium"
            onClick={() => navigate('/refrigerator')}
          >
            돌아가기
          </button>
        </div>
      )}
    </div>
  );
};

export default Refrigerator;
