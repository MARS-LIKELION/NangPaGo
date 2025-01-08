import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getLikes,
  getComments,
  getFavorites,
  getMyPageInfo,
} from '../../api/myPage.js';
import Header from '../../components/common/Header';
import Footer from '../../components/common/Footer';
import { FaHeart, FaStar, FaComment, FaClipboard } from 'react-icons/fa';

function Profile() {
  const [myPageInfo, setMyPageInfo] = useState({});
  const [activeTab, setActiveTab] = useState('likes');
  const [recipes, setRecipes] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [totalCounts, setTotalCounts] = useState({
    likes: 0,
    comments: 0,
    favorites: 0,
    community: 0,
  });
  const isFetchingRef = useRef(false);
  const observerRef = useRef(null);
  const observerInstance = useRef(null);

  const navigate = useNavigate();

  useEffect(() => {
    getMyPageInfo()
      .then((info) => {
        setMyPageInfo(info);
        setTotalCounts({
          likes: info.likeCount || 0,
          comments: info.commentCount || 0,
          favorites: info.favoriteCount || 0,
          community: info.refrigeratorCount || 0,
        });
      })
      .catch(console.error);
  }, []);

  useEffect(() => {
    const fetchTabData = async () => {
      if (isFetchingRef.current || !hasMore) return;

      isFetchingRef.current = true;

      try {
        let data = { content: [], last: true };
        switch (activeTab) {
          case 'comments':
            data = await getComments(page, 5);
            break;
          case 'likes':
            data = await getLikes(page, 5);
            break;
          case 'favorites':
            data = await getFavorites(page, 5);
            break;
          default:
            data = { content: [], last: true };
        }

        setRecipes((prev) => [
          ...prev,
          ...data.content.filter(
            (recipe) => !prev.some((r) => r.id === recipe.id),
          ),
        ]);

        setHasMore(!data.last);
      } catch (error) {
        console.error('Failed to fetch tab data:', error);
      } finally {
        isFetchingRef.current = false;
      }
    };

    fetchTabData();
  }, [activeTab, page]);

  useEffect(() => {
    setRecipes([]);
    setPage(0);
    setHasMore(true);
  }, [activeTab]);

  useEffect(() => {
    if (observerInstance.current) {
      observerInstance.current.disconnect();
    }

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasMore && !isFetchingRef.current) {
          setPage((prevPage) => prevPage + 1);
        }
      },
      { threshold: 1.0 },
    );

    if (observerRef.current) {
      observerInstance.current.observe(observerRef.current);
    }

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [hasMore]);

  const handleRecipeClick = (id) => {
    navigate(`/recipe/${id}`);
  };

  const renderRecipeList = () => {
    if (!recipes.length && !hasMore) {
      return <div>레시피가 없습니다.</div>;
    }

    return (
      <div className="grid grid-cols-1 gap-4">
        {recipes.map((recipe) => (
          <div
            key={recipe.id}
            className="flex h-[90px] overflow-hidden border rounded-lg shadow-sm cursor-pointer"
            onClick={() => handleRecipeClick(recipe.id)}
          >
            <div className="w-[90px] h-[90px] flex-shrink-0 bg-gray-100 overflow-hidden">
              <img
                src={recipe.mainImage}
                alt={recipe.name}
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.target.onerror = null;
                }}
              />
            </div>
            <div className="flex flex-col justify-between p-4 flex-grow">
              <div>
                <h3
                  className="font-bold mb-2 line-clamp-1 overflow-hidden text-ellipsis whitespace-nowrap"
                  style={{
                    display: '-webkit-box',
                    WebkitLineClamp: 1,
                    WebkitBoxOrient: 'vertical',
                    overflow: 'hidden',
                  }}
                >
                  {recipe.name}
                </h3>
                <div className="flex flex-wrap gap-2">
                  <span className="py-1 px-2 text-sm rounded-md text-black bg-[var(--secondary-color)]">
                    {recipe.category}
                  </span>
                  <span className="py-1 px-2 text-sm rounded-md text-black bg-[var(--secondary-color)]">
                    {recipe.cookingMethod}
                  </span>
                  <span className="py-1 px-2 text-sm rounded-md text-black bg-[var(--secondary-color)]">
                    {recipe.calorie}kcal
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
        {hasMore && <div ref={observerRef} style={{ height: '1px' }} />}
      </div>
    );
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <div>
        <Header />
        <div className="flex-1 px-6 bg-white">
          <div className="flex items-center justify-between py-4 border-b">
            <div>
              <div className="text-lg font-medium">{myPageInfo.nickName}</div>
              <div className="text-gray-500">
                연결된 계정 : {myPageInfo.providerName}
              </div>
            </div>
            <button
              onClick={() => navigate('/my-page/modify')}
              className="text-[var(--secondary-color)] text-[20px]"
            >
              &gt;
            </button>
          </div>

          <div className="grid grid-cols-4 border-b">
            <button
              onClick={() => setActiveTab('community')}
              className={`flex flex-col items-center py-3 ${
                activeTab === 'community'
                  ? 'text-black border-b-2 border-[var(--secondary-color)]'
                  : 'text-gray-400'
              }`}
            >
              <FaClipboard className="mb-1 text-orange-400" />
              <span className="text-sm">게시글</span>
              <span className="text-xs">{totalCounts.community}</span>
            </button>
            <button
              onClick={() => setActiveTab('comments')}
              className={`flex flex-col items-center py-3 ${
                activeTab === 'comments'
                  ? 'text-black border-b-2 border-[var(--secondary-color)]'
                  : 'text-gray-400'
              }`}
            >
              <FaComment className="mb-1 text-red-400" />
              <span className="text-sm">댓글</span>
              <span className="text-xs">{totalCounts.comments}</span>
            </button>
            <button
              onClick={() => setActiveTab('likes')}
              className={`flex flex-col items-center py-3 ${
                activeTab === 'likes'
                  ? 'text-black border-b-2 border-[var(--secondary-color)]'
                  : 'text-gray-400'
              }`}
            >
              <FaHeart className="mb-1 text-red-600" />
              <span className="text-sm">좋아요</span>
              <span className="text-xs">{totalCounts.likes}</span>
            </button>
            <button
              onClick={() => setActiveTab('favorites')}
              className={`flex flex-col items-center py-3 ${
                activeTab === 'favorites'
                  ? 'text-black border-b-2 border-[var(--secondary-color)]'
                  : 'text-gray-400'
              }`}
            >
              <FaStar className="mb-1 text-yellow-400" />
              <span className="text-sm">즐겨찾기</span>
              <span className="text-xs">{totalCounts.favorites}</span>
            </button>
          </div>

          <div className="py-4">{renderRecipeList()}</div>
        </div>
      </div>

      <Footer />
    </div>
  );
}

export default Profile;
