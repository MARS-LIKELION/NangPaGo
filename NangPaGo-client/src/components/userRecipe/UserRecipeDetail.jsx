import { useState } from 'react';
import { AiFillHeart, AiOutlineEdit } from 'react-icons/ai';
import { useNavigate } from 'react-router-dom';
import CookingStepsSlider from '../recipe/CookingStepsSlider';
import ToggleButton from '../button/ToggleButton';
import DeleteModal from '../modal/DeleteModal';
import DeleteSuccessModal from '../modal/DeleteSuccessModal';
import { deleteUserRecipe } from '../../api/userRecipe';

function UserRecipeDetail({ data, isLoggedIn }) {
  if (!data) return <p className="text-center text-gray-500">레시피를 불러오는 중...</p>;

  const navigate = useNavigate();
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);

  // 글작성, 글수정 핸들러
  const handleCreateClick = () => {
    navigate('/user-recipe/create', { state: { from: window.location.pathname } });
  };
  const handleEditClick = () => {
    navigate(`/user-recipe/${data.id}/modify`, { state: { from: window.location.pathname } });
  };

  // "글삭제" 버튼 클릭 시 삭제 모달 오픈
  const handleDeleteClick = () => {
    setIsDeleteModalOpen(true);
  };

  // 삭제 모달에서 '삭제'를 누르면 삭제 API 호출 후 성공 모달로 전환
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

  // 성공 모달 확인 버튼 클릭 시 목록 페이지로 이동
  const handleSuccessModalClose = () => {
    setIsSuccessModalOpen(false);
    navigate('/user-recipe/list');
  };

  // ToggleButton에 전달할 액션 배열
  const actions = data.isOwnedByUser
    ? [
        { label: '글작성', onClick: handleCreateClick },
        { label: '글수정', onClick: handleEditClick },
        { label: '글삭제', onClick: handleDeleteClick }
      ]
    : [{ label: '글작성', onClick: handleCreateClick }];

  return (
    <div className="max-w-4xl mx-auto bg-white shadow-md rounded-lg p-6 relative">
      <div className="md:flex md:items-start gap-8">
        <div className="md:w-1/2">
          <img
            src={data.mainImageUrl}
            alt={data.title}
            className="w-full max-h-80 object-cover rounded-2xl shadow-md mb-6"
          />
        </div>
        <div className="md:w-1/2">
          <div className="flex justify-between items-center mt-6 relative">
            <h1 className="text-3xl font-bold">{data.title}</h1>
            {data.isOwnedByUser && <ToggleButton actions={actions} />}
            <div className="flex items-center gap-1 text-gray-600">
              <AiFillHeart className="text-red-500 text-xl" />
              <span>{data.likeCount}</span>
            </div>
          </div>
          <p className="text-gray-700 mt-4">{data.content}</p>
          <h2 className="text-lg font-semibold mt-6 mb-2">재료</h2>
          <ul className="list-disc list-inside">
            {(data.ingredients || []).map((ingredient, index) => (
              <li key={index} className="text-gray-700">{ingredient}</li>
            ))}
          </ul>
        </div>
      </div>
      <div className="mt-8">
        <h2 className="text-lg font-semibold mb-2">조리 과정</h2>
        {data.manuals && data.manuals.length > 0 ? (
          <CookingStepsSlider 
            manuals={data.manuals} 
            manualImages={data.imageUrls || []} 
            isUserRecipe={true}  
          />
        ) : (
          <p className="text-gray-500">조리 과정 정보가 없습니다.</p>
        )}
      </div>
      {/* 삭제 확인 모달 */}
      <DeleteModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onDelete={confirmDelete}
      />
      {/* 삭제 성공 모달 */}
      <DeleteSuccessModal
        isOpen={isSuccessModalOpen}
        onClose={handleSuccessModalClose}
        message="레시피가 삭제되었습니다."
      />
    </div>
  );
}

export default UserRecipeDetail;
