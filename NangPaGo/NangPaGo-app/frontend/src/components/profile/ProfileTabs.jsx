import { FaComment, FaHeart, FaStar } from 'react-icons/fa';

const ProfileTabs = ({ activeTab, totalCounts, onTabChange }) => (
  <div className="grid grid-cols-3 border-b">
    {[
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
      {
        key: 'comments',
        label: '댓글',
        icon: <FaComment className="mb-1 text-red-400" />,
        count: totalCounts.comments,
      },
    ].map((tab) => (
      <button
        key={tab.key}
        onClick={() => onTabChange(tab.key)}
        className={`flex flex-col items-center py-3 ${
          activeTab === tab.key
            ? 'text-primary border-b-2 border-primary bg-white rounded-b-none'
            : 'text-text-400 bg-white '
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
