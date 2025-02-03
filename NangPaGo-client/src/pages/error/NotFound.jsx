import ErrorGuidePage from '../../components/common/ErrorGuidePage.jsx';

function NotFound() {
  const message = `
요청하신 페이지가 존재하지 않거나,
주소를 잘못 입력하였습니다.`;

  return (
    <ErrorGuidePage title="페이지를 찾을 수 없습니다." message={message} showLoginButton={false}/>
  );
}

export default NotFound;
