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
          <div className="relative ml-1.5 min-w-[20px] flex items-center">
            <span 
              className={`absolute left-0 text-sm transition-all duration-300 ${
                isHeartActive 
                  ? 'opacity-100 transform translate-y-0' 
                  : 'opacity-0 transform -translate-y-2'
              }`}
            >
              {likeCount}
            </span>
            <span 
              className={`absolute left-0 text-sm transition-all duration-300 ${
                !isHeartActive 
                  ? 'opacity-100 transform translate-y-0' 
                  : 'opacity-0 transform translate-y-2'
              }`}
            >
              {likeCount}
            </span>
          </div>
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
