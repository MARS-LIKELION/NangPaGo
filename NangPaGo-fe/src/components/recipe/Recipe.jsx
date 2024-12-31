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

  // 좋아요와 즐겨찾기 상태를 동시에 가져오는 함수
  const fetchStatuses = async () => {
    try {
      const [likeResponse, favoriteResponse] = await Promise.all([
        axiosInstance.get(
          `/api/recipe/${recipe.id}/like/status?email=${userEmail}`,
        ),
        axiosInstance.get(
          `/api/recipe/${recipe.id}/favorite/status?email=${userEmail}`,
        ),
      ]);
      setIsHeartActive(likeResponse.data);
      setIsStarActive(favoriteResponse.data);
    } catch (error) {
      console.error('상태를 불러오는 중 오류가 발생했습니다.', error);
    }
  };

  // 좋아요 토글
  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const response = await axiosInstance.post(
        `/api/recipe/${recipe.id}/like/toggle`,
        {
          recipeId: recipe.id,
          email: userEmail,
        },
      );
      setIsHeartActive(response.data.isLiked); // 서버 응답 값 반영
      fetchStatuses(); // 상태 다시 불러오기
    } catch (error) {
      console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다.', error);
    }
  };

  // 즐겨찾기 토글
  const toggleStar = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const response = await axiosInstance.post(
        `/api/recipe/${recipe.id}/favorite/toggle`,
        {
          recipeId: recipe.id,
          email: userEmail,
        },
      );
      setIsStarActive(response.data.isFavorite); // 서버 응답 값 반영
      fetchStatuses(); // 상태 다시 불러오기
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
        <div className="mt-4 flex items-center justify-between">
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
        <div>
          <p className="text-gray-600 text-[14px] mt-2">{recipe.category}</p>
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
