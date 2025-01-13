import { AiFillHeart } from 'react-icons/ai';
import { FaCommentAlt } from 'react-icons/fa';
import { styles } from '../../components/common/Image';

function CommunityCard({ item, onClick }) {
  return (
    <li
      className="border rounded-lg overflow-hidden shadow-md flex flex-col cursor-pointer"
      onClick={() => onClick(item.id)}
    >
      {/* 이미지 */}
      <img
        src={item.imageUrl}
        alt={item.title}
        className={styles.imageList}
      />
      {/* 텍스트 */}
      <div className="p-4 space-y-2">
        <div className="flex items-center gap-4 text-gray-600">
          <div className="flex items-center gap-1">
            <AiFillHeart className="text-red-500 text-lg" />
            <span>{item.likeCount}</span>
          </div>
          <div className="flex items-center gap-1">
            <FaCommentAlt className="text-gray-500 text-lg" />
            <span>{item.commentCount}</span>
          </div>
        </div>
        <h2 className="text-lg font-semibold truncate">{item.title}</h2>
        <p className="text-sm text-gray-500 line-clamp-2">{item.content}</p>
      </div>
    </li>
  );
}

export default CommunityCard;
