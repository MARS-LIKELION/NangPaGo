import { Fragment } from 'react';
import { IMAGE_STYLES } from '../../common/styles/Image';
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
      <div className="w-full px-4">
        <div className="w-full h-[70vh] overflow-hidden rounded-md flex justify-center items-center bg-gray-50">
          <img
            src={community.imageUrl}
            alt={community.title}
            className={IMAGE_STYLES.mainImage}
          />
        </div>
      </div>
      <div className="mt-2 flex items-center justify-between px-4">
        <PostStatusButton post={post} isLoggedIn={isLoggedIn} />
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
