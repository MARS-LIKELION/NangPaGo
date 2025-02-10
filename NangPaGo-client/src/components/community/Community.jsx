import { Fragment, useState, useEffect } from 'react';
import { FaHeart, FaRegHeart } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';
import usePostStatus from '../../hooks/usePostStatus';

const formatDate = (date) =>
  new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(date));

const renderContentLines = (content) =>
  content.split(/\r?\n/).map((line, index) => (
    <Fragment key={index}>
      {line}
      <br />
    </Fragment>
  ));

function Community({ post, data: community, isLoggedIn }) {
  const {
    isHeartActive,
    likeCount,
    toggleHeart,
  } = usePostStatus(post, isLoggedIn);

  const [prevCount, setPrevCount] = useState(likeCount);
  const [isIncreasing, setIsIncreasing] = useState(true);

  useEffect(() => {
    if (prevCount !== likeCount) {
      setIsIncreasing(likeCount > prevCount);
      setPrevCount(likeCount);
    }
  }, [likeCount, prevCount]);

  return (
    <>
      <div className="mt-6 px-4">
        <h1 className="text-xl font-bold">{community.title}</h1>
        <div className="mt-2 flex flex-col text-gray-500 text-xs">
          <span>
            <strong className="mr-2">{community.nickname}</strong>
              <span>・ {formatDate(community.updatedAt)} </span>
          </span>
        </div>
      </div>
      <div className="mt-4 px-4">
        <img
          src={community.imageUrl}
          alt={community.title}
          className={IMAGE_STYLES.mainImage}
        />
      </div>
      <div className="mt-2 flex items-center justify-between px-4">
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
                  isIncreasing 
                    ? 'opacity-100 transform translate-y-0' 
                    : 'opacity-0 transform -translate-y-2'
                }`}
              >
                {likeCount}
              </span>
              <span 
                className={`absolute left-0 text-sm transition-all duration-300 ${
                  !isIncreasing 
                    ? 'opacity-100 transform translate-y-0' 
                    : 'opacity-0 transform translate-y-2'
                }`}
              >
                {likeCount}
              </span>
            </div>
          )}
        </button>
      </div>
      <div className="mt-4 px-4">
        <p className="text-gray-700 text-sm">
          {renderContentLines(community.content)}
        </p>
      </div>
    </>
  );
}

export default Community;
