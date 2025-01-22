import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function Error() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [errorTitle, setErrorTitle] = useState('');
  const [errorDescription, setErrorDescription] = useState('');

  useEffect(() => {
    const title = searchParams.get('title');
    const description = searchParams.get('description');
    if (title) {
      setErrorTitle(title);
    }
    if (description) {
      setErrorDescription(description);
    }
  }, [searchParams]);

  return (
    <div
      className="bg-white shadow-md mx-auto min-h-screen flex flex-col items-center justify-center px-4 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg"
      style={{ paddingTop: '2rem', paddingBottom: '2rem' }}
    >
      <div className="w-full max-w-lg bg-white p-8 rounded-lg shadow-md">
        <div className="flex items-center space-x-4 mb-8">
          <img src="/logo.png" className="w-16 h-16" />
          <h1 className="text-2xl font-bold leading-tight text-primary" style={{ whiteSpace: 'pre-line' }}>
            {errorTitle || 'Error'}
          </h1>
        </div>
        <div className="flex items-start space-x-2 mb-8">
          <span className="text-primary-600 text-3xl font-bold leading-none">|</span>
          <p className="text-gray-600 text-md flex-1" style={{ whiteSpace: 'pre-line' }}>
            {errorDescription || '요청하신 작업 중 오류가 발생했습니다. 다시 시도해주세요.'}
          </p>
        </div>
      </div>
      <div className="fixed bottom-8 left-0 right-0 flex justify-center space-x-4 px-4 z-50">
        <button
          onClick={() => navigate('/', { replace: true })}
          className="bg-primary-500 text-white px-6 py-2 rounded-lg shadow-md hover:bg-primary-600 transition w-[200px]"
        >
          홈으로 돌아가기
        </button>
        <button
          onClick={() => navigate('/login', { replace: true })}
          className="bg-primary-500 text-white px-6 py-2 rounded-lg shadow-md hover:bg-primary-600 transition w-[200px]"
        >
          로그인 페이지로 이동
        </button>
      </div>
    </div>
  );
}

export default Error;
