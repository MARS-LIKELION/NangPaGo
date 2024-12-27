import IngredientList from './IngredientList';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';

function Recipe({ recipe }) {
  return (
    <div className="recipe-detail-container bg-white shadow-md mx-auto w-[375px] min-h-screen p-4">
      <img
        src={recipe.mainImage}
        alt={recipe.name}
        className="w-full h-48 object-cover rounded-md"
      />

      <h1 className="text-xl font-bold mt-4">{recipe.name}</h1>
      <p className="text-gray-600 text-sm">{recipe.category}</p>

      <IngredientList ingredients={recipe.ingredients} />

      <CookingSteps steps={recipe.manuals} stepImages={recipe.manualImages} />

      <NutritionInfo
        calories={recipe.calories}
        fat={recipe.fat}
        carbs={recipe.carbohydrates}
        protein={recipe.protein}
        sodium={recipe.sodium}
      />
    </div>
  );
}

export default Recipe;
