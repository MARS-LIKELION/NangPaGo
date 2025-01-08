import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import axiosInstance from '../../api/axiosInstance';
import Header from '../common/Header';
import Footer from '../common/Footer';
import UpdateUserInfoModal from '../../common/modal/UpdateUserInfoModal';

const UserInfoModify = () => {
  const [userInfo, setUserInfo] = useState({});
  const [nickname, setNickname] = useState('');
  const [isNicknameAvailable, setIsNicknameAvailable] = useState(false);
  const [isNicknameAvailableMessage, setIsNicknameAvailableMessage] =
    useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const loginState = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(loginState.email);

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axiosInstance.get('/api/user/profile');
        setUserInfo(response.data.data);
        setNickname(response.data.data.nickname);
        setIsNicknameAvailableMessage('');
        console.log('사용자 정보:', response.data.data);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    };
    if (isLoggedIn) {
      fetchUserInfo();
    }
  }, [isLoggedIn]);

  const handleNicknameCheck = async () => {
    const trimmedNickname = nickname.trim();
    setNickname(trimmedNickname);

    try {
      const response = await axiosInstance.get(`/api/user/profile/check?nickname=${trimmedNickname}`);
      console.log('닉네임 중복 확인 응답:', response);

      setIsNicknameAvailable(true);
      setIsNicknameAvailableMessage('사용 가능한 닉네임입니다.');
    } catch (error) {
      setIsNicknameAvailable(false);
      
      if (error.response?.status === 400) {
        setIsNicknameAvailableMessage(error.response.data.message);
      } else {
        console.error('닉네임 중복 확인 실패:', error);
        setIsNicknameAvailableMessage('닉네임 중복 확인에 실패했습니다.');
      }
    }
  };

  const handleSubmit = async () => {
    if (!isNicknameAvailable) {
      setIsNicknameAvailableMessage('닉네임 중복 확인을 해주세요.');
      return;
    }

    try {
      const userInfoRequestDto = {
        nickname: nickname,
      };
      await axiosInstance.put('/api/user/profile', userInfoRequestDto);
      setIsModalOpen(true);
    } catch (error) {
      console.error('Failed to update user info:', error);
    }
  };

  return (
    <div className="bg-white mx-auto w-[375px] min-h-screen flex flex-col">
      <div className="flex-grow">
        <Header />
        <div className="px-6 bg-white">
          <h3 className="text-xl font-bold mb-6 text-gray-800 text-center">
            회원정보 수정
          </h3>
          <div>
            <div className="mb-4">
              <label className="block text-gray-500 mb-2">닉네임</label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => {
                    setNickname(e.target.value);
                    setIsNicknameAvailable(false);
                    setIsNicknameAvailableMessage('');
                  }}
                  className="flex-1 p-2 border border-gray-300 rounded"
                />
                <button
                  onClick={handleNicknameCheck}
                  className="bg-yellow-400 text-white px-4 py-2 rounded whitespace-nowrap"
                >
                  중복 확인
                </button>
              </div>
              {isNicknameAvailableMessage && (
                <p
                  className={`mt-2 ${isNicknameAvailable ? 'text-green-600' : 'text-red-600'}`}
                >
                  {isNicknameAvailableMessage}
                </p>
              )}
            </div>
            <div className="mb-4">
              <label className="block text-gray-500 mb-2">이메일</label>
              <input
                type="email"
                value={userInfo.email}
                className="w-full p-2 rounded border border-gray-F bg-gray-200"
                disabled
              />
            </div>
          </div>
        </div>
      </div>
      <div className="px-6 mb-6 mt-auto">
        <button
          onClick={handleSubmit}
          className={`w-full py-2 rounded mt-6 ${isNicknameAvailable ? 'bg-yellow-400 text-white' : 'bg-yellow-200 text-white cursor-not-allowed'}`}
          disabled={!isNicknameAvailable}
        >
          회원정보 저장하기
        </button>
        <UpdateUserInfoModal isOpen={isModalOpen} onClose={handleCloseModal} />
      </div>
      <Footer />
    </div>
  );
};

export default UserInfoModify;
