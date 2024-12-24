import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/common/Header';
import axiosInstance from '../api/axiosInstance.js';

function Home() {
  // 사용자 정보를 저장할 상태 변수
  const [user, setUser] = useState(undefined);
  // 페이지 이동을 위한 useNavigate 훅
  const navigate = useNavigate();

  // 로그아웃을 처리하는 함수
  // const handleLogout = async () => {
  //   try {
  //     // 로그아웃 API 호출
  //     const response = await axiosInstance.post('/auth/logout');
  //     if (response.status === 200) {
  //       console.log('로그아웃 성공');
  //       setUser(null); // 사용자 정보 초기화
  //       navigate('/'); // 홈 페이지로 이동
  //     }
  //   } catch (error) {
  //     console.error('로그아웃 요청 중 오류 발생:', error);
  //   }
  // };

  // 컴포넌트가 마운트될 때 사용자 정보를 가져오는 useEffect 훅
  useEffect(() => {
    axiosInstance
      .get('/auth/me') // 사용자 정보 API 호출
      .then((response) => {
        if (response.status === 200) {
          setUser(response.data); // 사용자 정보 상태 업데이트
        }
      })
      .catch(() => {
        console.log('로그인되지 않은 상태');
        setUser(null); // 사용자 정보 초기화
      });
  }, [axiosInstance, navigate]);

  // 사용자 정보가 아직 로드되지 않은 경우 로딩 화면 표시
  if (user === undefined) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
        <h1>로딩 중...</h1>
      </div>
    );
  }

  // 사용자 정보가 로드된 경우 메인 화면 표시
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <Header isLoggedIn={user} user={user} handleLogout={handleLogout} />
    </div>
  );
}

export default Home;
