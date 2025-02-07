import { Link } from 'react-router-dom';
import { FaRegHeart, FaRegComment } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';
import { PAGE_STYLES } from '../../common/styles/ListPage';

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
          <div className="flex items-center gap-1 text-gray-600">
            <FaRegHeart className="text-xl" />
            {recipe.likeCount}
          </div>
          <div className="flex items-center gap-1 text-gray-600">
            <FaRegComment className="text-xl" />
            {recipe.commentCount}
          </div>
        </div>

        <h2 className="text-lg font-semibold">{recipe.name}</h2>
        <div className="flex flex-wrap gap-2">
          {recipe.ingredientsDisplayTag.map((tag, index) => (
            <span
              key={index}
              className={PAGE_STYLES.tag}
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
