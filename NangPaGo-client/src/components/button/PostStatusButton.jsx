import { FaHeart, FaRegHeart, FaStar, FaRegStar } from 'react-icons/fa';
import { useEffect, useState } from 'react';
import usePostStatus from '../../hooks/usePostStatus';
import LoginModal from '../modal/LoginModal';

function PostStatusButton({ post, isLoggedIn, className = '' }) {
  const postStatus = usePostStatus(post, isLoggedIn);

  const {
    isHeartActive,
    likeCount,
    toggleHeart,
    modalState,
    setModalState,
    isStarActive = false,
    toggleStar = () => {},
  } = postStatus;

  return (
    <div className={`flex items-center justify-between gap-4 ${className}`}>
      {toggleHeart && (
        <LikeButton
          isHeartActive={isHeartActive}
          likeCount={likeCount}
          toggleHeart={toggleHeart}
        />
      )}
      {post.type === 'recipe' && (
        <FavoriteButton isStarActive={isStarActive} toggleStar={toggleStar} />
      )}
      {modalState.type === 'login' && (
        <LoginModal
          isOpen={true}
          onClose={() => setModalState({ type: null, data: null })}
          description={modalState.data}
        />
      )}
    </div>
  );
}

function LikeButton({ isHeartActive, likeCount, toggleHeart, className = '' }) {
  const [prevCount, setPrevCount] = useState(likeCount);
  const [isIncreasing, setIsIncreasing] = useState(true);

  useEffect(() => {
    if (prevCount !== likeCount) {
      setIsIncreasing(likeCount > prevCount);
      setPrevCount(likeCount);
    }
  }, [likeCount, prevCount]);

  return (
    <button
      className={`flex items-center bg-white ${
        isHeartActive ? 'text-red-500' : 'text-gray-600'
      } transition-all duration-300 ${className}`}
      onClick={toggleHeart}
    >
      <div
        className={`transform transition-transform duration-300 ${
          isHeartActive ? 'animate-heart-bounce' : ''
        }`}
      >
        {isHeartActive ? <FaHeart className="text-2xl" /> : <FaRegHeart className="text-2xl" />}
      </div>

      {/* likeCount가 null이면 숫자만 투명하게 유지 */}
      <div className="relative ml-1.5 min-w-[20px] flex items-center">
        <span
          className={`absolute left-0 text-sm transition-all duration-300 ${
            likeCount !== null
              ? isIncreasing
                ? 'opacity-100 transform translate-y-0'
                : 'opacity-0 transform -translate-y-2 invisible'
              : 'opacity-0 invisible'
          }`}
        >
          {likeCount}
        </span>
        <span
          className={`absolute left-0 text-sm transition-all duration-300 ${
            likeCount !== null
              ? !isIncreasing
                ? 'opacity-100 transform translate-y-0'
                : 'opacity-0 transform translate-y-2 invisible'
              : 'opacity-0 invisible'
          }`}
        >
          {likeCount}
        </span>
      </div>
    </button>
  );
}

function FavoriteButton({ isStarActive, toggleStar, className = '' }) {
  return (
    <button
      className={`bg-white ${
        isStarActive ? 'text-primary' : 'text-gray-600'
      } transition-all duration-300 ${className}`}
      onClick={toggleStar}
    >
      {isStarActive ? <FaStar className="text-2xl" /> : <FaRegStar className="text-2xl" />}
    </button>
  );
}

export { LikeButton, FavoriteButton };
export default PostStatusButton;
