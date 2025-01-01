import SocialLoginButton from '../../components/login/SocialLoginButton.jsx';
import { SOCIAL_BUTTON_STYLES } from '../../components/util/auth.js';

const API_HOST = import.meta.env.VITE_HOST;

function Login() {
  const handleLoginClick = (provider) => {
    window.location.href = `${API_HOST}/api/oauth2/authorization/${provider}`;
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col items-center justify-center">
      <img src="/logo.png" alt="Logo" className="w-32 h-auto mb-6" />
      <div className="flex flex-col items-center space-y-4 w-full max-w-xs px-4">
        {Object.keys(SOCIAL_BUTTON_STYLES).map((provider) => (
          <SocialLoginButton
            key={provider}
            provider={provider}
            onClick={() => handleLoginClick(provider)}
          />
        ))}
      </div>
    </div>
  );
}

export default Login;
