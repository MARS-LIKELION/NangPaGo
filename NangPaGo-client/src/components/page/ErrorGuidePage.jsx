import { Link } from 'react-router-dom';

// eslint-disable-next-line react/prop-types
function ErrorGuidePage({ title, message, showLoginButton = true }) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-8 rounded-lg relative flex flex-col items-center max-w-[300px] w-[calc(100%-32px)]">
        <p className="text-center text-lg font-semibold text-gray-600 mb-2">
          {title}
        </p>
        <p className="text-center text-sm text-gray-500 whitespace-pre-line">
          {message}
        </p>
        <div className="flex gap-2 mt-4">
          <Link to="/" className="bg-gray-400 text-white px-5 py-3 rounded-lg">
            홈으로
          </Link>
          {showLoginButton && (
            <Link
              to="/login"
              className="bg-primary text-white px-5 py-3 rounded-lg"
            >
              로그인
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}

export default ErrorGuidePage;
