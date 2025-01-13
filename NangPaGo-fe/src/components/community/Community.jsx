import { Fragment, useEffect, useState } from 'react';
import { FaHeart, FaTimes } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import CommunityComment from './comment/CommunityComment';
import Header from '../common/Header';
import Footer from '../common/Footer';
import CreateButton from '../common/CreateButton';
import {
  getLikeCount,
  getLikeStatus,
  deleteCommunity,
  toggleLike,
} from '../../api/community';

const maskEmail = (email) => {
  if (!email) return '';
  const visiblePart = email.slice(0, 3);
  return `${visiblePart}***`;
};

function Community({ community }) {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    fetchLikeData();
  }, [community.id]);

  const fetchLikeData = async () => {
    try {
      const count = await getLikeCount(community.id);
      const status = await getLikeStatus(community.id);
      setLikeCount(count);
      setIsHeartActive(status);
    } catch (error) {
      console.error(error.message);
    }
  };

  const toggleHeart = async () => {
    try {
      const isLiked = await toggleLike(community.id);
      setIsHeartActive(isLiked);
      setLikeCount((prev) => (isLiked ? prev + 1 : prev - 1));
    } catch (error) {
      console.error(error.message);
    }
  };

  const handleDeleteClick = async () => {
    if (window.confirm('정말로 글을 삭제하시겠습니까?')) {
      try {
        await deleteCommunity(community.id);
        navigate('/community');
      } catch (error) {
        console.error(error.message);
        alert('글 삭제에 실패했습니다.');
      }
    }
  };

  const handleCreateClick = () => navigate('/community/new');
  const handleEditClick = () => navigate(`/community/${community.id}/modify`);
  const toggleMenu = () => setIsMenuOpen((prev) => !prev);

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <Header />
      <div>
        <div className="mt-6 px-4">
          <h1 className="text-xl font-bold">{community.title}</h1>
          <div className="mt-2 flex flex-col text-gray-500 text-xs">
            <span>
              <strong>{maskEmail(community.email)}</strong>
            </span>
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
          <p className="text-gray-700 text-sm">
            {community.content.split(/\r?\n/).map((line, index) => (
              <Fragment key={index}>
                {line}
                <br />
              </Fragment>
            ))}
          </p>
        </div>
        <CommunityComment communityId={community.id} />
      </div>
      <Footer />

      <div className="fixed bottom-10 right-[calc((100vw-375px)/2+16px)] z-9999">
        <div className="relative">
          {community.isOwnedByUser ? (
            <>
              <CreateButton
                onClick={toggleMenu}
                basePositionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
                icon={
                  isMenuOpen ? <FaTimes className="text-xl text-white" /> : null
                }
              />
              <ul
                className={`absolute bottom-1 right-16 flex flex-col items-end gap-3 ${
                  isMenuOpen ? 'opacity-100 visible' : 'opacity-0 invisible'
                } transition-opacity duration-300`}
              >
                <li
                  className={`bg-[var(--secondary-color)] text-white px-4 py-2 rounded-md shadow-md hover:bg-opacity-90 transform ${
                    isMenuOpen
                      ? 'translate-x-0 opacity-100 delay-[200ms]'
                      : 'translate-x-[50px] opacity-0'
                  } transition-all duration-300`}
                >
                  <button onClick={handleCreateClick} className="text-white ">
                    글작성
                  </button>
                </li>
                <li
                  className={`bg-[var(--secondary-color)] text-white px-4 py-2 rounded-md shadow-md hover:bg-opacity-90 transform ${
                    isMenuOpen
                      ? 'translate-x-0 opacity-100 delay-[100ms]'
                      : 'translate-x-[50px] opacity-0'
                  } transition-all duration-300`}
                >
                  <button onClick={handleEditClick} className="text-white ">
                    글수정
                  </button>
                </li>
                <li
                  className={`bg-[var(--secondary-color)] text-white px-4 py-2 rounded-md shadow-md hover:bg-opacity-90 transform ${
                    isMenuOpen
                      ? 'translate-x-0 opacity-100 delay-[0ms]'
                      : 'translate-x-[50px] opacity-0'
                  } transition-all duration-300`}
                >
                  <button onClick={handleDeleteClick} className="text-white ">
                    글삭제
                  </button>
                </li>
              </ul>
            </>
          ) : (
            <CreateButton
              onClick={handleCreateClick}
              basePositionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
              ariaLabel="글쓰기 버튼"
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default Community;
