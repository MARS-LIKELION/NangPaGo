import IngredientList from './IngredientList';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';
import Header from '../common/Header';

function Recipe({ recipe }) {
  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
      <Header />
      <div className="mt-4 mx-5">
        <img
          src={recipe.mainImage}
          alt={recipe.name}
          className="w-full h-48 object-cover rounded-md"
        />
      </div>
      <h1 className="text-xl font-bold mt-4 mx-5">{recipe.name}</h1>
      <p className="text-gray-600 text-sm mx-5">{recipe.category}</p>
      <div className="mx-5">
        <h2 className="text-lg font-semibold mt-4">재료</h2>
        <ul className="grid grid-cols-2 gap-1 mt-2">
          {recipe.ingredients.split('소스')[0]
            .split(/,(?!\s*\n)/) // Split by comma not followed by newline
            .flatMap((item) => 
              item.split('\n').map((subItem) => subItem.trim().replace(/[^a-zA-Z0-9가-힣\s.]/g, ' '))
            )
            .map((item, index) => (
              <li key={`ingredient-${index}`} className="text-gray-700 text-sm">
                {item}
              </li>
            ))}
        </ul>
        {recipe.ingredients.includes('소스') && (
          <div>
            <h2 className="text-lg font-semibold mt-4">소스</h2>
            <ul className="grid grid-cols-3 gap-2 mt-2">
              {recipe.ingredients.split('소스')[1]
                .split(',')
                .flatMap((item) => 
                  item.split('\n').map((subItem) => subItem.trim().replace(/[^a-zA-Z0-9가-힣\s.]/g, ''))
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
      <div className="mx-5">
        {recipe.manuals.map((step, index) => (
          <div key={index} className="mb-10">
            {index === 0 && <h2 className="text-lg font-semibold mt-4">요리 과정</h2>}
            <div className="my-10">
              <CookingSteps steps={[step]} stepImages={[recipe.manualImages[index]]} />
            </div>
          </div>
        ))}
      </div>

      <div className="mx-5">
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
