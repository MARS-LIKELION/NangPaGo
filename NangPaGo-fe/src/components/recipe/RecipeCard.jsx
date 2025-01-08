import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getLikeCount } from '../../api/recipe';
import { AiFillHeart } from 'react-icons/ai';

function RecipeCard({ recipe }) {
  const [likeCount, setLikeCount] = useState(null);

  useEffect(() => {
    const fetchLikeCount = async () => {
      try {
        const count = await getLikeCount(recipe.id);
        setLikeCount(count);
      } catch (error) {
        console.error('좋아요 개수를 가져오는 중 오류 발생:', error);
      }
    };
    fetchLikeCount();
  }, [recipe.id]);

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
        <div className="text-sm text-gray-600 flex items-center gap-1">
          <AiFillHeart className="text-red-500 text-xl" />
          {likeCount !== null ? likeCount : '0'}
        </div>
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
