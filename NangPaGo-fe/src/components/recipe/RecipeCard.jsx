import { Link } from 'react-router-dom';

function RecipeCard({ recipe }) {
  
  return (
    <Link
      to={`/recipe/${recipe.id}`}
      className="block overflow-hidden rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
    >
      <img
        src={recipe.recipeImageUrl}
        alt={recipe.name}
        className="w-full h-48 object-cover"
      />
      <div className="p-4 flex flex-col gap-2">
        <h3 className="text-lg font-semibold">{recipe.name}</h3>
        <div className="flex flex-wrap gap-2">
          {recipe.ingredientsDisplayTag.map((tag, index) => (
            <span
              key={index}
              className="bg-[var(--secondary-color)] text-black text-sm font-medium px-2 py-1 rounded"
            >
              {tag}
            </span>
          ))}
        </div>
      </div>
    </Link>
  );
}

export default RecipeCard;
