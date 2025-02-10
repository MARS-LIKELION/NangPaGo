// UserRecipeDetail.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import usePostStatus from '../../hooks/usePostStatus';
import CookingStepsSlider from '../userRecipe/CookingStepsSlider';
import ToggleButton from '../button/ToggleButton';
import DeleteModal from '../modal/DeleteModal';
import DeleteSuccessModal from '../modal/DeleteSuccessModal';
import RecipeButton from '../button/RecipeButton';
import { deleteUserRecipe } from '../../api/userRecipe';

function UserRecipeDetail({ data, isLoggedIn }) {
  if (!data) return <p className="text-center text-gray-500">레시피를 불러오는 중...</p>;

  const navigate = useNavigate();
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);

  const formatDate = (date) =>
    new Intl.DateTimeFormat('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).format(new Date(date));

  const post = { type: 'user-recipe', id: data.id };
  const {
    isHeartActive,
    likeCount,
    toggleHeart,
  } = usePostStatus(post, isLoggedIn);

  const handleCreateClick = () => {
    navigate('/user-recipe/create', { state: { from: window.location.pathname } });
  };
  const handleEditClick = () => {
    navigate(`/user-recipe/${data.id}/modify`, { state: { from: window.location.pathname } });
  };
  const handleDeleteClick = () => {
    setIsDeleteModalOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await deleteUserRecipe(data.id);
      setIsDeleteModalOpen(false);
      setIsSuccessModalOpen(true);
    } catch (error) {
      console.error(error);
      alert('삭제에 실패했습니다.');
    }
  };

  const handleSuccessModalClose = () => {
    setIsSuccessModalOpen(false);
    navigate('/user-recipe/list');
  };

  const actions = data.isOwnedByUser
    ? [
        { label: '글작성', onClick: handleCreateClick },
        { label: '글수정', onClick: handleEditClick },
        { label: '글삭제', onClick: handleDeleteClick }
      ]
    : [{ label: '글작성', onClick: handleCreateClick }];

  const hasManuals = Array.isArray(data.manuals) && data.manuals.length > 0;

  return (
    <div className="max-w-4xl mx-auto bg-white shadow-md rounded-lg p-6 relative">
      <div className="md:flex md:items-start gap-8">
        {/* 좌측 열: 대표이미지만 표시 */}
        <div className="md:w-1/2">
          <img
            src={data.mainImageUrl}
            alt={data.title}
            className="w-full max-h-80 object-cover rounded-2xl shadow-md mb-6"
          />
        </div>

        {/* 우측 열: 제목, 내용, 재료 등 */}
        <div className="md:w-1/2">
          <div className="flex justify-between items-center mt-6 relative">
            <h1 className="text-2xl font-bold">{data.title}</h1>
            {data.isOwnedByUser && <ToggleButton actions={actions} />}
            <RecipeButton
              isHeartActive={isHeartActive}
              likeCount={likeCount}
              toggleHeart={toggleHeart}
              className="ml-4"
            />
          </div>
          <div className="mt-2 text-gray-500 text-xs">
            <strong className="mr-2">{data.nickname}</strong>
            <span>・</span>
            <span className="ml-2">{formatDate(data.updatedAt)}</span>
          </div>
          <p className="text-gray-700 mt-4">{data.content}</p>

          <h2 className="text-lg font-semibold mt-6 mb-2">재료</h2>
          <ul className="list-disc list-inside space-y-2">
            {(data.ingredients || []).map((ingredient, index) => (
              <li key={index} className="text-gray-700 flex justify-between">
                <span className="font-semibold">{ingredient.name}</span>
                <span className="ml-6">{ingredient.amount}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* 조리 과정 영역 – 모든 메뉴얼(조리 과정)을 슬라이더로 표시 */}
      <div className="mt-8">
        <h2 className="text-lg font-semibold mb-2">조리 과정</h2>
        {hasManuals ? (
          <CookingStepsSlider
            manuals={data.manuals.map((manual) => ({
              description: manual.description,
              imageUrl: manual.imageUrl,
            }))}
            manualImages={data.manuals.map((manual) => manual.imageUrl)}
            isUserRecipe={true}
          />
        ) : (
          <p className="text-gray-500">조리 과정 정보가 없습니다.</p>
        )}
      </div>

      <DeleteModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onDelete={confirmDelete}
      />

      <DeleteSuccessModal
        isOpen={isSuccessModalOpen}
        onClose={handleSuccessModalClose}
        message="레시피가 삭제되었습니다."
      />
    </div>
  );
}

export default UserRecipeDetail;
