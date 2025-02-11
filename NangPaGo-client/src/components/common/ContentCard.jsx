import { Link } from 'react-router-dom';
import { FaHeart, FaRegHeart, FaRegComment } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';
import { PAGE_STYLES } from '../../common/styles/ListPage';

function ContentCard({ type, data }) {
  return (
    <Link
      to={`/${type}/${data.id}`}
      className="block overflow-hidden rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300"
    >
      <img
        src={data.mainImageUrl}
        alt={data.title}
        className={IMAGE_STYLES.imageList}
      />
      <div className="p-4 flex flex-col gap-2">
        <div className="text-sm text-text-400 flex items-center gap-4">
          <div className="flex items-center gap-1 text-gray-600">
            {data.isLiked ? (
              <FaHeart className="text-xl text-red-500" />
            ) : (
              <FaRegHeart className="text-xl" />
            )}
            {data.likeCount}
          </div>
          <div className="flex items-center gap-1 text-gray-600">
            <FaRegComment className="text-xl" />
            {data.commentCount}
          </div>
        </div>
        <h2 className="text-lg font-semibold truncate">{data.title}</h2>
        {type === 'recipe' ? (
          <div className="flex flex-wrap gap-2">
            {data.ingredientsDisplayTag.map((tag, index) => (
              <span key={index} className={PAGE_STYLES.tag}>
                {tag}
              </span>
            ))}
          </div>
        ) : (
          <p className="text-sm text-text-600 line-clamp-2">{data.content}</p>
        )}
      </div>
    </Link>
  );
}

export default ContentCard;
