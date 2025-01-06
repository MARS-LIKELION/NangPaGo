import { useState, useEffect } from 'react';
import axios from 'axios';
import Header from '../common/Header';
import Footer from '../common/Footer';

const UserInfoModify = () => {
  const [userInfo, setUserInfo] = useState({});
  const [nickname, setNickname] = useState('');

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('/api/v1/members/me', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
          },
        });
        setUserInfo(response.data);
        setNickname(response.data.nickname);
        console.log('사용자 정보:', response.data);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    };

    fetchUserInfo();
  }, []);

  const handleNicknameCheck = async () => {
    try {
      const response = await axios.get(`/api/users/checkNickname/${nickname}`);
      console.log('닉네임 중복 확인 응답:', response);

      if (response.data.available) {
        alert('사용 가능한 닉네임입니다.');
      } else {
        alert('이미 사용 중인 닉네임입니다.');
      }
    } catch (error) {
      console.error('닉네임 중복 확인 실패:', error);
      alert('닉네임 중복 확인에 실패했습니다.');
    }
  };

  const handleSubmit = async () => {
    try {
      await axios.patch(
        '/api/v1/members/me',
        { nickname },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
          },
        },
      );
      alert('회원정보가 수정되었습니다.');
    } catch (error) {
      console.error('Failed to update user info:', error);
    }
  };

  return (
    <div className="bg-white mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <div>
        <Header />
        <div className="px-6 bg-white">
          <h3 className="text-xl font-bold mb-6 text-gray-800 text-center">
            회원정보 수정
          </h3>
          <div>
            <div className="mb-4">
              <label className="block text-gray-700 mb-2">이름</label>
              <input
                type="text"
                value={userInfo.name || ''}
                className="w-full p-2 border border-gray-300 rounded"
              />
            </div>
            <div className="mb-4">
              <label className="block text-gray-500 mb-2">닉네임 (필수)</label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  className="flex-1 p-2 border border-gray-300 rounded"
                />
                <button
                  onClick={handleNicknameCheck}
                  className="bg-yellow-400 text-white px-4 py-2 rounded whitespace-nowrap"
                >
                  중복 확인
                </button>
              </div>
            </div>
            <div className="mb-4">
              <label className="block text-gray-500 mb-2">이메일 (필수)</label>
              <input
                type="email"
                value={userInfo.email}
                className="w-full p-2 rounded border border-gray-300"
                disabled
              />
            </div>
            <div className="mt-6">
              <div className="text-gray-500 mb-2">계정 정보</div>
              <div className="border-t border-yellow-400">
                <div className="flex justify-between py-2 border-b">
                  <span className="text-gray-600">연결 계정</span>
                  <span className="text-gray-800">
                    {userInfo.provider === 'NAVER'
                      ? '네이버'
                      : userInfo.provider === 'KAKAO'
                        ? '카카오'
                        : userInfo.provider === 'GOOGLE'
                          ? '구글'
                          : ''}
                  </span>
                </div>
              </div>
            </div>
            <button
              onClick={handleSubmit}
              className="w-full bg-yellow-400 text-white py-2 rounded mt-6"
            >
              회원정보 저장하기
            </button>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default UserInfoModify;
