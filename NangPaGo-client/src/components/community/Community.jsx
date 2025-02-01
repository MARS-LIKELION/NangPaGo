import { Fragment } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaHeart } from 'react-icons/fa';
import ToggleButton from '../button/ToggleButton';
import { IMAGE_STYLES } from '../../common/styles/Image';
import { deleteCommunity } from '../../api/community';
import usePostStatus from '../../hooks/usePostStatus';

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

function Community({ data: community, isLoggedIn }) {
  const {
    isHeartActive,
    likeCount,
    toggleHeart,
    modalState,
    setModalState,
  } = usePostStatus("community", community.id, isLoggedIn);

  const navigate = useNavigate();

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

  const actions = community.isOwnedByUser
    ? [
        { label: '글작성', onClick: handleCreateClick },
        { label: '글수정', onClick: handleEditClick },
        { label: '글삭제', onClick: handleDeleteClick },
      ]
    : [{ label: '글작성', onClick: handleCreateClick }];

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
      <ToggleButton actions={actions}/>
    </>
  );
}

export default Community;
