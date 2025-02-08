import { FaHeart, FaRegHeart, FaStar, FaRegStar  } from 'react-icons/fa';

function RecipeButton({
  isHeartActive,
  isStarActive,
  likeCount,
  toggleHeart,
  toggleStar,
  className = '',
}) {
  return (
    <div className={`flex items-center justify-between gap-4 ${className}`}>
      <button
        className={`flex items-center bg-white ${
          isHeartActive ? 'text-red-500' : 'text-gray-600'
        } transition-all duration-300`}
        onClick={toggleHeart}
      >
        <div className={`transform transition-transform duration-300 ${
          isHeartActive ? 'animate-heart-bounce' : ''
        }`}>
          {isHeartActive ? (
            <FaHeart className="text-2xl" />
          ) : (
            <FaRegHeart className="text-2xl" />
          )}
        </div>
        {likeCount !== null && (
          <span className="text-sm ml-1">{likeCount}</span>
        )}
      </button>
      <button
        className={`bg-white ${
          isStarActive ? 'text-primary' : 'text-gray-600'
        }`}
        onClick={toggleStar}
      >
        {isStarActive ? (
          <FaStar className="text-2xl" />
        ) : (
          <FaRegStar className="text-2xl" />
        )}
      </button>
    </div>
  );
}

export default RecipeButton;
