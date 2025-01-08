import { useState, useEffect } from 'react';
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
  const [likedRecipes, setLikedRecipes] = useState([]);
  const [commentedRecipes, setCommentedRecipes] = useState([]);
  const [favoriteRecipes, setFavoriteRecipes] = useState([]);
  const [page, setPage] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    // 유저 정보를 가져옴
    getMyPageInfo().then(setMyPageInfo).catch(console.error);

    // 탭 데이터 가져오기
    const fetchCurrentTabData = async () => {
      try {
        if (activeTab === 'comments') {
          const data = await getComments(page, 5);
          setCommentedRecipes(data.content);
        } else if (activeTab === 'likes') {
          const data = await getLikes(page, 5);
          setLikedRecipes(data.content);
        } else if (activeTab === 'favorites') {
          const data = await getFavorites(page, 5);
          setFavoriteRecipes(data.content);
        }
      } catch (error) {
        console.error('탭 데이터 로딩 실패:', error);
      }
    };

    fetchCurrentTabData();
  }, [activeTab, page]);

  // 현재 탭의 데이터를 반환
  const getCurrentRecipes = () => {
    switch (activeTab) {
      case 'likes':
        return likedRecipes;
      case 'comments':
        return commentedRecipes;
      case 'favorites':
        return favoriteRecipes;
      default:
        return [];
    }
  };

  const renderRecipeList = () => {
    const currentRecipes = getCurrentRecipes();

    if (!currentRecipes?.length) {
      return <div>레시피가 없습니다.</div>;
    }

    return (
      <div className="grid grid-cols-1 gap-4">
        {currentRecipes.map((recipe) => (
          <div key={recipe.id} className="flex h-[90px] overflow-hidden">
            <div className="w-[90px] h-[90px] flex-shrink-0">
              <img
                src={recipe.mainImage}
                alt={recipe.name}
                className="w-full h-full object-cover"
              />
            </div>
            <div className="flex flex-col justify-between p-4 flex-grow">
              <div>
                <h3 className="font-bold mb-2">{recipe.name}</h3>
                <div className="flex flex-wrap gap-2">
                  <span className="px-3 py-1 text-sm">
                    {recipe.category} &nbsp;
                    {recipe.cookingMethod} &nbsp;
                    {recipe.calorie}kcal
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
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
              className={`flex flex-col items-center py-3 ${activeTab === 'community' ? 'text-black border-b-2 border-[var(--secondary-color)]' : 'text-400'}`}
            >
              <FaClipboard className="mb-1 text-orange-400" />
              <span className="text-sm">게시글</span>
              <span className="text-xs">
                {myPageInfo.refrigeratorCount || 0}
              </span>
            </button>
            <button
              onClick={() => setActiveTab('comments')}
              className={`flex flex-col items-center py-3 ${activeTab === 'comments' ? 'text-black border-b-2 border-[var(--secondary-color)]' : 'text-400'}`}
            >
              <FaComment className="mb-1 text-red-400" />
              <span className="text-sm">댓글</span>
              <span className="text-xs">{myPageInfo.commentCount || 0}</span>
            </button>
            <button
              onClick={() => setActiveTab('likes')}
              className={`flex flex-col items-center py-3 ${activeTab === 'likes' ? 'text-black border-b-2 border-[var(--secondary-color)]' : 'text-400'}`}
            >
              <FaHeart className="mb-1 text-red-600" />
              <span className="text-sm">좋아요</span>
              <span className="text-xs">{myPageInfo.likeCount || 0}</span>
            </button>
            <button
              onClick={() => setActiveTab('favorites')}
              className={`flex flex-col items-center py-3 ${activeTab === 'favorites' ? 'text-black border-b-2 border-[var(--secondary-color)]' : 'text-400'}`}
            >
              <FaStar className="mb-1 text-yellow-400" size={18} />
              <span className="text-sm">즐겨찾기</span>
              <span className="text-xs">{myPageInfo.favoriteCount || 0}</span>
            </button>
          </div>

          <div className="py-4">
            {activeTab === 'community' && <div>게시글 목록</div>}
            {activeTab === 'comments' && renderRecipeList()}
            {activeTab === 'likes' && renderRecipeList()}
            {activeTab === 'favorites' && renderRecipeList()}
          </div>
        </div>
      </div>

      <div className="py-2 flex justify-center gap-2">
        <button
          onClick={() => setPage((prev) => Math.max(0, prev - 1))}
          disabled={page === 0}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          이전
        </button>
        <span className="px-3 py-1">페이지 {page + 1}</span>
        <button
          onClick={() => setPage((prev) => prev + 1)}
          disabled={!getCurrentRecipes()?.length}
          className="px-3 py-1 border rounded disabled:opacity-50"
        >
          다음
        </button>
      </div>

      <div className="mt-auto">
        <div className="px-6 mb-4">
          <button className="w-full bg-yellow-400 text-white py-2 rounded">
            회원탈퇴
          </button>
        </div>
        <Footer />
      </div>
    </div>
  );
}

export default Profile;
