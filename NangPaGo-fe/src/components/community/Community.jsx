import axiosInstance from '../../api/axiosInstance.js';
import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { FaHeart } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom'; // useNavigate 추가
import CommunityComment from './comment/CommunityComment';
import Header from '../common/Header';
import Footer from '../common/Footer';
import { getLikeCount } from '../../api/community.js';
import CreateButton from '../common/CreateButton.jsx';
import TopButton from '../common/TopButton.jsx';

function Community({ community }) {
  const { email: userEmail } = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(userEmail);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);

  const [isHeartActive, setIsHeartActive] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

  const navigate = useNavigate(); // useNavigate 호출

  useEffect(() => {
    fetchLikeCount();
    if (isLoggedIn) {
      fetchLikeStatus();
    }
  }, [isLoggedIn, community.id]);

  const handleCreateClick = () => {
    navigate('/community/new'); // 페이지 이동
  };

  const fetchLikeCount = async () => {
    try {
      const count = await getLikeCount(community.id);
      setLikeCount(count);
    } catch (error) {
      console.error('좋아요 수를 가져오는 중 오류가 발생했습니다:', error);
    }
  };

  const fetchLikeStatus = async () => {
    try {
      const response = await axiosInstance.get(
        `/api/community/${community.id}/like/status`,
      );
      setIsHeartActive(response.data.data);
    } catch (error) {
      console.error('좋아요 상태를 불러오는 중 오류가 발생했습니다.', error);
    }
  };

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const response = await axiosInstance.post(
        `/api/community/${community.id}/like/toggle`,
      );
      const isLiked = response.data.data.liked;
      setIsHeartActive(isLiked);
      setLikeCount((prev) => (isLiked ? prev + 1 : prev - 1));
    } catch (error) {
      console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다:', error);
    }
  };

  const maskEmail = (email) => {
    const visiblePart = email.slice(0, 3);
    return `${visiblePart}***`;
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <Header />
      <div>
        <div className="mt-6 px-4">
          <h1 className="text-xl font-bold">{community.title}</h1>
          <div className="mt-2 flex flex-col text-gray-500 text-xs">
            <span><strong>{maskEmail(community.email)}</strong></span>
            <span>
              {new Intl.DateTimeFormat('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
              }).format(new Date(community.updatedAt))}
            </span>
          </div>
        </div>
        <div className="mt-4 px-4">
          <img
            src={community.imageUrl}
            alt={community.title}
            className="w-full h-48 object-cover rounded-md"
          />
        </div>

        <div className="mt-2 flex items-center justify-between px-4">
          <div className="flex items-center gap-2">
            <button
              className={`flex items-center ${
                isHeartActive ? 'text-red-500' : 'text-gray-500'
              }`}
              onClick={toggleHeart}
            >
              <FaHeart className="text-2xl" />
              <span className="text-sm ml-1">{likeCount}</span>
            </button>
          </div>
        </div>

        <div className="mt-4 px-4 min-h-[10rem]">
          <p className="text-gray-700 text-sm">{community.content}</p>
        </div>
        <CommunityComment communityId={community.id} />
      </div>
      <Footer />
      <CreateButton
        onClick={handleCreateClick}
        isTopButtonVisible={isTopButtonVisible}
        basePositionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
      />
      {isTopButtonVisible && (
        <TopButton
          offset={100}
          positionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
        />
      )}
    </div>
  );
}

export default Community;
