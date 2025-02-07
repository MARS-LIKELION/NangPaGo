import { PAGE_STYLES } from '../../common/styles/ListPage';

function RecipeInfo({ recipe }) {
  return (
    <div>
      <h1 className="text-xl font-bold">{recipe.name}</h1>
      <div className="flex gap-2 mt-2">
        {recipe.mainIngredient && (
          <span className={PAGE_STYLES.tag}>
            {recipe.mainIngredient}
          </span>
        )}
        {recipe.category && (
          <span className={PAGE_STYLES.tag}>
            {recipe.category}
          </span>
        )}
        {recipe.cookingMethod && (
          <span className={PAGE_STYLES.tag}>
            {recipe.cookingMethod}
          </span>
        )}
      </div>
    </div>
  );
}

export default RecipeInfo;
