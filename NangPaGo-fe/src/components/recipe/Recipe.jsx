import axiosInstance from '../../api/axiosInstance.js';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';
import { FaHeart, FaStar } from 'react-icons/fa';
import RecipeComment from './comment/RecipeComment';
import Header from '../common/Header';
import Footer from '../common/Footer';

function Recipe({ recipe }) {
  const { email: userEmail } = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(userEmail);

  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  useEffect(() => {
    if (isLoggedIn) {
      fetchStatuses();
    }
  }, [isLoggedIn]);

  const fetchStatuses = async () => {
    try {
      const [likeResponse, favoriteResponse] = await Promise.all([
        axiosInstance.get(`/api/recipe/${recipe.id}/like/status`),
        axiosInstance.get(`/api/recipe/${recipe.id}/favorite/status`),
      ]);
      setIsHeartActive(likeResponse.data.data);
      setIsStarActive(favoriteResponse.data);
    } catch (error) {
      console.error('상태를 불러오는 중 오류가 발생했습니다.', error);
    }
  };

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const response = await axiosInstance.post(
        `/api/recipe/${recipe.id}/like/toggle`,
      );
      setIsHeartActive(response.data.data.liked);
    } catch (error) {
      console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다.', error);
    }
  };

  const toggleStar = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const response = await axiosInstance.post(
        `/api/recipe/${recipe.id}/favorite/toggle`,
      );
      console.log('Response:', response);
      setIsStarActive(response.data.data.favorited);
    } catch (error) {
      console.error('즐겨찾기 상태를 변경하는 중 오류가 발생했습니다.', error);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <Header />
      <div className="px-4">
        <div className="mt-4">
          <img
            src={recipe.mainImage}
            alt={recipe.name}
            className="w-full h-48 object-cover rounded-md"
          />
        </div>
        <div className="mt-4 flex items-center pb-3 justify-between">
          <h1 className="text-xl font-bold">{recipe.name}</h1>
          <div className="flex gap-2">
            <button
              className={`bg-white ${isHeartActive ? 'text-red-500' : 'text-gray-500'}`}
              onClick={toggleHeart}
            >
              <FaHeart className="bg-white text-2xl" />
            </button>
            <button
              className={`bg-white ${isStarActive ? 'text-yellow-500' : 'text-gray-500'}`}
              onClick={toggleStar}
            >
              <FaStar className="bg-white text-2xl" />
            </button>
          </div>
        </div>
        <div className="flex gap-2">
          {recipe.mainIngredient && (
            <p className="bg-[var(--secondary-color)] text-black text-sm font-medium px-2 py-1 rounded">
              {recipe.mainIngredient}
            </p>
          )}
          {recipe.category && (
            <p className="bg-[var(--secondary-color)] text-black text-sm font-medium px-2 py-1 rounded">
              {recipe.category}
            </p>
          )}
          {recipe.cookingMethod && (
            <p className="bg-[var(--secondary-color)] text-black text-sm font-medium px-2 py-1 rounded">
              {recipe.cookingMethod}
            </p>
          )}
        </div>
        <div className="mt-7">
          <h2 className="text-lg font-semibold mb-3">재료</h2>
          <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
            {recipe.ingredients
              .split('소스')[0]
              .split(/,|\n/)
              .map((ingredient, index) => (
                <li key={`ingredient-${index}`} className="font-medium">
                  {ingredient.replace(/[^가-힣a-zA-Z0-9()./×\s]/gi, '').trim()}
                </li>
              ))}
          </ul>
        </div>
        {recipe.ingredients.includes('소스') && (
          <div className="mt-7">
            <h2 className="text-lg font-semibold mb-3">소스</h2>
            <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
              {recipe.ingredients
                .split('소스')[1]
                .split(/,|\n/)
                .map((sauce, index) => (
                  <li key={`sauce-${index}`} className="font-medium">
                    {sauce.replace(/[^가-힣a-zA-Z0-9()./×\s]/gi, '').trim()}
                  </li>
                ))}
            </ul>
          </div>
        )}
        <div className="mt-7">
          <h2 className="text-lg font-semibold">요리 과정</h2>
          {recipe.manuals.map((step, index) => (
            <div key={index} className="mt-4">
              <CookingSteps
                steps={[step]}
                stepImages={[recipe.manualImages[index]]}
              />
            </div>
          ))}
        </div>
        <div className="mt-7">
          <NutritionInfo
            calories={recipe.calorie}
            fat={recipe.fat}
            carbs={recipe.carbohydrate}
            protein={recipe.protein}
            sodium={recipe.natrium}
          />
        </div>
        <RecipeComment recipeId={recipe.id} />
      </div>
      <Footer />
    </div>
  );
}

export default Recipe;
