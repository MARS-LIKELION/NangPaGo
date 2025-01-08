import { MdKitchen } from 'react-icons/md';
import { FaComment, FaHeart, FaStar } from 'react-icons/fa';

const ProfileTabs = ({ activeTab, totalCounts, onTabChange }) => (
  <div className="grid grid-cols-4 border-b">
    {[
      {
        key: 'refrigerator',
        label: '냉장고',
        icon: <MdKitchen className="mb-1 text-orange-400" />,
        count: totalCounts.refrigerator,
      },
      {
        key: 'comments',
        label: '댓글',
        icon: <FaComment className="mb-1 text-red-400" />,
        count: totalCounts.comments,
      },
      {
        key: 'likes',
        label: '좋아요',
        icon: <FaHeart className="mb-1 text-red-600" />,
        count: totalCounts.likes,
      },
      {
        key: 'favorites',
        label: '즐겨찾기',
        icon: <FaStar className="mb-1 text-yellow-400" />,
        count: totalCounts.favorites,
      },
    ].map((tab) => (
      <button
        key={tab.key}
        onClick={() => onTabChange(tab.key)}
        className={`flex flex-col items-center py-3 ${
          activeTab === tab.key
            ? 'text-black border-b-2 border-[var(--secondary-color)]'
            : 'text-gray-400'
        }`}
      >
        {tab.icon}
        <span className="text-sm">{tab.label}</span>
        <span className="text-xs">{tab.count}</span>
      </button>
    ))}
  </div>
);

export default ProfileTabs;
