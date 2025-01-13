import axiosInstance from '../../api/axiosInstance.js';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';
import { FaHeart, FaStar } from 'react-icons/fa';
import RecipeComment from './comment/RecipeComment';
import Header from '../common/Header';
import Footer from '../common/Footer';
import { getLikeCount } from '../../api/recipe.js';

function Recipe({ recipe }) {
  const { email: userEmail } = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(userEmail);

  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

  useEffect(() => {
    fetchLikeCount();
    if (isLoggedIn) {
      fetchStatuses();
    }
  }, [isLoggedIn, recipe.id]);

  const fetchLikeCount = async () => {
    try {
      const count = await getLikeCount(recipe.id);
      setLikeCount(count);
    } catch (error) {
      console.error('좋아요 수를 가져오는 중 오류가 발생했습니다:', error);
    }
  };

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
      const isLiked = response.data.data.liked;
      setIsHeartActive(isLiked);
      setLikeCount((prev) => (isLiked ? prev + 1 : prev - 1));
    } catch (error) {
      console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다:', error);
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
      setIsStarActive(response.data.data.favorited);
    } catch (error) {
      console.error('즐겨찾기 상태를 변경하는 중 오류가 발생했습니다.', error);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <Header />
      <div>
        <div className="mt-4 px-4">
          <img
            src={recipe.mainImage}
            alt={recipe.name}
            className="w-full h-48 object-cover rounded-md"
          />
        </div>

        <div className="mt-2 flex items-center justify-between px-4">
          <div className="flex items-center gap-2">
            <button
              className={`flex items-center ${
                isHeartActive ? 'text-red-500' : 'text-gray-500'
              }`}
              onClick={toggleHeart}
            >
              <FaHeart className="text-2xl" />
              <span className="text-sm ml-1">{likeCount}</span>
            </button>
          </div>

          <div>
            <button
              className={` ${isStarActive ? 'text-[var(--primary-color)]' : 'text-gray-500'}`}
              onClick={toggleStar}
            >
              <FaStar className="text-2xl" />
            </button>
          </div>
        </div>

        <h1 className="text-xl font-bold mt-2 px-4">{recipe.name}</h1>

        <div className="flex gap-2 mt-2 px-4">
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

        <div className="mt-7 px-4">
          <h2 className="text-lg font-semibold mb-3">재료</h2>
          <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
            {recipe.ingredients
              .split(',')
              .map((ingredient, index) => (
                <li key={`ingredient-${index}`} className="font-medium">
                  {ingredient.replace(/[^가-힣a-zA-Z0-9()./×\s]/gi, '').trim()}
                </li>
              ))}
          </ul>
        </div>
        <div className="mt-7 px-4">
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
        <div className="mt-7 px-4">
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
