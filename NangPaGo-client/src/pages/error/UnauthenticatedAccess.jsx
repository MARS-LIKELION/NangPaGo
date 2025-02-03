import ErrorGuidePage from '../../components/common/ErrorGuidePage.jsx';

function UnauthenticatedAccess() {
  const message = `이 페이지에 접근하려면
로그인이 필요합니다.

로그인 후 다시 시도해주세요.`;

  return <ErrorGuidePage title="로그인 후 이용해주세요" message={message} />;
}

export default UnauthenticatedAccess;
