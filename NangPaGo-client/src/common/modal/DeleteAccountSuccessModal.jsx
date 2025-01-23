import { useDispatch } from 'react-redux';
import { logout } from '../../slices/loginSlice.js';
import axiosInstance from '../../api/axiosInstance.js';

function DeleteAccountSuccessModal({ isOpen, onClose }) {
  const dispatch = useDispatch();

  if (!isOpen) return null;

  const handleConfirm = async () => {
    try {
      // 서버 로그아웃
      await axiosInstance.post('/api/logout');
      // Redux 상태 초기화
      dispatch(logout());
      // '/'로 이동
      window.location.href = '/';
    } catch (error) {
      console.error('로그아웃 실패:', error);
    }
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-md flex flex-col items-center max-w-[400px]">
        <p className="text-center text-lg mb-4">
          회원탈퇴가 완료되었습니다.
          
        </p>
        <button
          onClick={handleConfirm}
          className="bg-gray-300 text-white px-5 py-2 rounded"
        >
          확인
        </button>
      </div>
    </div>
  );
}

export default DeleteAccountSuccessModal;
