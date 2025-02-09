import { FaHeart, FaRegHeart, FaRegComment } from 'react-icons/fa';
import { IMAGE_STYLES } from '../../common/styles/Image';

function CommunityCard({ item, onClick }) {
  return (
    <li
      className="border rounded-md overflow-hidden shadow-md flex flex-col cursor-pointer"
      onClick={() => onClick(item.id)}
    >
      <img src={item.imageUrl} alt={item.title} className={IMAGE_STYLES.imageList} />
      <div className="p-4 space-y-2">
        <div className="flex items-center gap-4 text-text-400">
          <div className="flex items-center gap-1 text-gray-600">
            {item.isLiked ? (
              <FaHeart className="text-xl text-red-500" />
            ) : (
              <FaRegHeart className="text-xl" />
            )}
            <span>{item.likeCount}</span>
          </div>
          <div className="flex items-center gap-1 text-gray-600">
            <FaRegComment className="text-xl" />
            <span>{item.commentCount}</span>
          </div>
        </div>
        <h2 className="text-lg font-semibold truncate">{item.title}</h2>
        <p className="text-sm text-text-600 line-clamp-2">{item.content}</p>
      </div>
    </li>
  );
}

export default CommunityCard;
