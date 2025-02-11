import {useState} from 'react';
import axiosInstance from '../api/axiosInstance.js';
import {useNavigate} from 'react-router-dom';
import {SOCIAL_BUTTON_STYLES} from "../common/styles/CommonButton.js";

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);

    try {
      const response = await axiosInstance.post('/api/login/proc', formData);
      if (response.status === 200) {
        localStorage.setItem('isAuthenticated', 'true');
        navigate('/dashboard');
      }
    } catch (error) {
      console.log('로그인 에러: ', error);
    }
  };

  return (
      <div
          className="min-h-screen flex items-center justify-center bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
          <div>
            <div className="flex justify-center">
              <img src="/logo.png" alt="냉파고" className="w-32 h-auto mb-6" />
            </div>
            <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
              냉파고 관리자 페이지
            </h2>
          </div>
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm -space-y-px">
              <div>
                <label htmlFor="email-address" className="sr-only">
                  관리자 이메일
                </label>
                <input
                    id="email-address"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                    placeholder="이메일"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div>
                <label htmlFor="password" className="sr-only">
                  비밀번호
                </label>
                <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="current-password"
                    required
                    className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
              </div>
            </div>

            <div>
              <button
                  type="submit"
                  className={`${SOCIAL_BUTTON_STYLES.common.btn} group relative w-full flex justify-center py-2 px-4`}
              >
                로그인
              </button>
            </div>
          </form>
        </div>
      </div>
  );
} 
