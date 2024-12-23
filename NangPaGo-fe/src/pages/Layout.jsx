// Header 컴포넌트를 가져옵니다. 로그인 상태와 사용자 정보를 표시하기 위해 사용됩니다.
import Header from '../components/common/Header.jsx';

// Layout 컴포넌트 정의
const Layout = ({ isLoggedIn, user, handleLogout, children }) => {
  return (
    // 화면을 구성하는 기본 레이아웃 컨테이너
    <div className="flex flex-col min-h-screen">
      {/* 헤더 컴포넌트: 상단 고정 영역 */}
      {/* 로그인 상태, 사용자 정보, 로그아웃 핸들러를 Header에 전달 */}
      <Header isLoggedIn={isLoggedIn} user={user} handleLogout={handleLogout} />

      {/* 메인 컨텐츠 영역 */}
      <main className="flex-grow">{children}</main>
      {/* children은 Layout을 사용하는 컴포넌트에서 전달하는 동적인 내용 */}
    </div>
  );
};

// Layout 컴포넌트를 외부에서 사용할 수 있도록 export
export default Layout;
