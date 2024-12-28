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
        <IngredientList ingredients={recipe.ingredients} />
      </div>

      <div className="mx-5">
        <CookingSteps steps={recipe.manuals} stepImages={recipe.manualImages} />
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
