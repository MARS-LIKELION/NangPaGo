import { Link } from 'react-router-dom';
import { FaHeart, FaRegComment } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';

function RecipeCard({ recipe }) {
  return (
    <Link
      to={`/recipe/${recipe.id}`}
      className="block overflow-hidden rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
    >
      <img
        src={recipe.recipeImageUrl}
        alt={recipe.name}
        className={IMAGE_STYLES.imageList}
      />
      <div className="p-4 flex flex-col gap-2">
        <div className="text-sm text-text-400 flex items-center gap-4">
          <div className="flex items-center gap-1">
            <FaHeart className="text-red-500 text-2xl" />
            {recipe.likeCount}
          </div>
          <div className="flex items-center gap-1">
            <FaRegComment className="text-text-400 text-2xl" />
            {recipe.commentCount}
          </div>
        </div>

        <h2 className="text-md font-semibold">{recipe.name}</h2>
        <div className="flex flex-wrap gap-2">
          {recipe.ingredientsDisplayTag.map((tag, index) => (
            <span
              key={index}
              className="bg-white border text-text-400 text-xs px-2 py-1 rounded"
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
