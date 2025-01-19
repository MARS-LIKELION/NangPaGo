import { Link } from 'react-router-dom';
import { AiFillHeart } from 'react-icons/ai';
import { FaCommentAlt } from 'react-icons/fa';
import { styles } from '../common/Image';

function RecipeCard({ recipe }) {
  return (
    <Link
      to={`/recipe/${recipe.id}`}
      className="block overflow-hidden rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
    >
      <img
        src={recipe.recipeImageUrl}
        alt={recipe.name}
        className={styles.imageList}
      />
      <div className="p-4 flex flex-col gap-2">
        <div className="text-sm text-text-400 flex items-center gap-4">
          <div className="flex items-center gap-1">
            <AiFillHeart className="text-red-500 text-xl" />
            {recipe.likeCount}
          </div>
          <div className="flex items-center gap-1">
            <FaCommentAlt className="text-text-400 text-xl" />
            {recipe.commentCount}
          </div>
        </div>

        <h3 className="text-md font-semibold">{recipe.name}</h3>
        <div className="flex flex-wrap gap-2">
          {recipe.ingredientsDisplayTag.map((tag, index) => (
            <span
              key={index}
              className="bg-secondary text-text-900 text-sm px-2 py-1 rounded"
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
