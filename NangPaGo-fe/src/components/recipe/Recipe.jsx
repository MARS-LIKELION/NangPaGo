import { useState } from 'react';
import IngredientList from './IngredientList';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';
import Header from '../common/Header';
import { FaHeart, FaStar } from 'react-icons/fa';

function Recipe({ recipe }) {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);

  const toggleHeart = () => setIsHeartActive(!isHeartActive);
  const toggleStar = () => setIsStarActive(!isStarActive);

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
      <Header />
      <div className="mt-4 mx-4">
        <img
          src={recipe.mainImage}
          alt={recipe.name}
          className="w-full h-48 object-cover rounded-md"
        />
      </div>
      <div className="mt-4 mx-4 flex items-center justify-between">
        <h1 className="text-xl font-bold">{recipe.name}</h1>
        <div className="flex gap-2">
          <button
            className={`bg-white ${isHeartActive ? 'text-red-500' : 'text-gray-500'}`}
            onClick={toggleHeart}
          >
            <FaHeart className="bg-white text-2xl" />
          </button>
          <button
            className={`bg-white ${isStarActive ? 'text-[var(--primary-color)]' : 'text-gray-500'}`}
            onClick={toggleStar}
          >
            <FaStar className="bg-white text-2xl" />
          </button>
        </div>
      </div>
      <div className="mx-4">
        <p className="text-gray-600 text-[14px] mt-2">{recipe.category}</p>
      </div>
      <div className="mt-7 mx-4">
        <h2 className="text-lg font-semibold">재료</h2>
        <ul className="grid grid-cols-2 gap-2 mt-3">
          {recipe.ingredients
            .split('소스')[0]
            .split(/,(?!\s*\n)/) // Split by comma not followed by newline
            .flatMap((item) =>
              item
                .split('\n')
                .map((subItem) =>
                  subItem.trim().replace(/[^a-zA-Z0-9가-힣\s.]/g, ' '),
                ),
            )
            .map((item, index) => (
              <li key={`ingredient-${index}`} className="text-gray-700 text-sm">
                {item}
              </li>
            ))}
        </ul>
        {recipe.ingredients.includes('소스') && (
          <div className="mt-3">
            <h2 className="text-lg font-semibold">소스</h2>
            <ul className="grid grid-cols-2 gap-2 mt-3">
              {recipe.ingredients
                .split('소스')[1]
                .split(',')
                .flatMap((item) =>
                  item
                    .split('\n')
                    .map((subItem) =>
                      subItem.trim().replace(/[^a-zA-Z0-9가-힣\s.]/g, ' '),
                    ),
                )
                .map((item, index) => (
                  <li key={`sauce-${index}`} className="text-gray-700 text-sm">
                    {item}
                  </li>
                ))}
            </ul>
          </div>
        )}
      </div>
      <div className="mt-7 mx-4">
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
      <div className="mt-7 mx-4">
        <NutritionInfo
          calories={recipe.calories}
          fat={recipe.fat}
          carbs={recipe.carbohydrates}
          protein={recipe.protein}
          sodium={recipe.sodium}
        />
      </div>
    </div>
  );
}

export default Recipe;
