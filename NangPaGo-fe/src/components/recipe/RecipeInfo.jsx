function RecipeInfo({ recipe }) {
  return (
    <div>
      <h1 className="text-xl font-bold">{recipe.name}</h1>
      <div className="flex gap-2 mt-2">
        {recipe.mainIngredient && (
          <span className="bg-secondary text-black text-sm font-medium px-2 py-1 rounded-md">
            {recipe.mainIngredient}
          </span>
        )}
        {recipe.category && (
          <span className="bg-secondary text-black text-sm font-medium px-2 py-1 rounded-md">
            {recipe.category}
          </span>
        )}
        {recipe.cookingMethod && (
          <span className="bg-secondary text-black text-sm font-medium px-2 py-1 rounded-md">
            {recipe.cookingMethod}
          </span>
        )}
      </div>
    </div>
  );
}

export default RecipeInfo;
