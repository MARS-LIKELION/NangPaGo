import { FaHeart, FaStar } from 'react-icons/fa';

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
          isHeartActive ? 'text-red-500' : 'text-text-400'
        }`}
        onClick={toggleHeart}
      >
        <FaHeart className="text-2xl" />
        <span className="text-sm ml-1">{likeCount}</span>
      </button>
      <button
        className={`bg-white ${
          isStarActive ? 'text-primary' : 'text-text-400'
        }`}
        onClick={toggleStar}
      >
        <FaStar className="text-2xl" />
      </button>
    </div>
  );
}

export default RecipeButton;
