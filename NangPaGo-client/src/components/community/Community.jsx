import { Fragment, useState, useEffect } from 'react';
import { FaHeart, FaRegHeart } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';
import usePostStatus from '../../hooks/usePostStatus';
import PostStatusButton from '../button/PostStatusButton';

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
        <PostStatusButton
          isHeartActive={isHeartActive}
          likeCount={likeCount}
          toggleHeart={toggleHeart}
        />
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
