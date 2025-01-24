import { Fragment, useEffect, useState } from 'react';
import { FaHeart, FaTimes } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import Header from '../layout/header/Header';
import Footer from '../layout/Footer';
import CreateButton from '../button/CreateButton';
import { IMAGE_STYLES } from '../../common/styles/Image';
import {
  deleteCommunity,
  getLikeCount,
  getLikeStatus,
  toggleLike,
} from '../../api/community';

const maskEmail = (email) => (email ? `${email.slice(0, 3)}***` : '');

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

function Community({ data: community }) {
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
      console.error('Failed to fetch like data:', error);
    }
  };

  const toggleHeart = async () => {
    try {
      const isLiked = await toggleLike(community.id);
      setIsHeartActive(isLiked);
      setLikeCount((prev) => (isLiked ? prev + 1 : prev - 1));
    } catch (error) {
      console.error('Failed to toggle like:', error);
    }
  };

  const handleDeleteClick = async () => {
    if (window.confirm('정말로 글을 삭제하시겠습니까?')) {
      try {
        await deleteCommunity(community.id);
        navigate('/community');
      } catch (error) {
        console.error('Failed to delete community:', error);
        alert('글 삭제에 실패했습니다.');
      }
    }
  };

  const handleCreateClick = () =>
    navigate('/community/new', { state: { from: window.location.pathname } });

  const handleEditClick = () =>
    navigate(`/community/${community.id}/modify`, {
      state: { from: window.location.pathname },
    });

  const toggleMenu = () => setIsMenuOpen((prev) => !prev);

  const menuItemClass =
    'bg-secondary text-white px-4 py-2 rounded-md shadow-md hover:bg-opacity-90 transform transition-all duration-300';

  return (
    <>
    <div className="mt-6 px-4">
      <h1 className="text-xl font-bold">{community.title}</h1>
      <div className="mt-2 flex flex-col text-gray-500 text-xs">
        <span>
          <strong>{maskEmail(community.email)}</strong>
        </span>
        <span>{formatDate(community.updatedAt)}</span>
      </div>
    </div>
    <div className="mt-4 px-4">
      <img
        src={community.imageUrl}
        alt={community.title}
        className={IMAGE_STYLES.mainImage}
      />
    </div>
    <div className="mt-2 flex items-center justify-between px-4">
      <button
        className={`flex items-center bg-white ${
          isHeartActive ? 'text-red-500' : 'text-gray-500'
        }`}
        onClick={toggleHeart}
      >
        <FaHeart className="text-2xl" />
        <span className="text-sm ml-1">{likeCount}</span>
      </button>
    </div>
    <div className="mt-4 px-4">
      <p className="text-gray-700 text-sm">
        {renderContentLines(community.content)}
      </p>
    </div>
    <div className="fixed bottom-10 right-[calc((100vw-375px)/2+16px)] z-50">
      <div className="relative">
        {community.isOwnedByUser ? (
          <>
            <CreateButton
              onClick={toggleMenu}
              icon={
                isMenuOpen ? <FaTimes className="text-xl text-white" /> : null
              }
            />
            <ul
              className={`absolute bottom-12 right-20 flex flex-col items-end gap-3 ${
                isMenuOpen ? 'opacity-100 visible' : 'opacity-0 invisible'
              } transition-opacity duration-300`}
            >
              <li
                className={`${menuItemClass} ${
                  isMenuOpen
                    ? 'translate-x-0 opacity-100 delay-[200ms]'
                    : 'translate-x-[50px] opacity-0'
                }`}
              >
                <button
                  onClick={handleCreateClick}
                  className="bg-secondary text-white w-12 text-center py-2"
                >
                  글작성
                </button>
              </li>
              <li
                className={`${menuItemClass} ${
                  isMenuOpen
                    ? 'translate-x-0 opacity-100 delay-[100ms]'
                    : 'translate-x-[50px] opacity-0'
                }`}
              >
                <button
                  onClick={handleEditClick}
                  className="bg-secondary text-white w-12 text-center py-2"
                >
                  글수정
                </button>
              </li>
              <li
                className={`${menuItemClass} ${
                  isMenuOpen
                    ? 'translate-x-0 opacity-100 delay-[0ms]'
                    : 'translate-x-[50px] opacity-0'
                }`}
              >
                <button
                  onClick={handleDeleteClick}
                  className="bg-secondary text-white w-12 text-center py-2"
                >
                  글삭제
                </button>
              </li>
            </ul>
          </>
        ) : (
          <CreateButton onClick={handleCreateClick} />
        )}
      </div>
    </div>
  </>
  );
}

export default Community;
