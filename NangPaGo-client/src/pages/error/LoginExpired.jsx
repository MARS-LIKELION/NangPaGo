import ErrorGuidePage from '../../components/page/ErrorGuidePage.jsx';

function LoginExpired() {
  const message = `일정 기간 동안 활동이 없어
자동 로그아웃되었습니다.

안전한 서비스 이용을 위해
다시 로그인해 주세요.`;

  return (
    <ErrorGuidePage title="로그인 정보가 만료되었습니다." message={message} />
  );
}

export default LoginExpired;
