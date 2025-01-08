import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getLikeCount } from '../../api/recipe';
import { AiFillHeart } from 'react-icons/ai';
import { FaCommentAlt } from 'react-icons/fa';
import { fetchCommentCount } from '../../api/commentService';

function RecipeCard({ recipe }) {
  const [likeCount, setLikeCount] = useState(0);
  const [commentCount, setCommentCount] = useState(0);

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const [likeCountData, commentCountData] = await Promise.all([
          getLikeCount(recipe.id),
          fetchCommentCount(recipe.id),
        ]);

        setLikeCount(likeCountData || 0);
        setCommentCount(commentCountData || 0);
      } catch (error) {
        console.error(
          '좋아요 또는 댓글 데이터를 가져오는 중 오류 발생:',
          error,
        );
      }
    };

    fetchCounts();
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
        <div className="text-sm text-gray-600 flex items-center gap-4">
          <div className="flex items-center gap-1">
            <AiFillHeart className="text-red-500 text-xl" />
            {likeCount}
          </div>
          <div className="flex items-center gap-1">
            <FaCommentAlt className="text-gray-300 text-xl" />
            {commentCount}
          </div>
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
