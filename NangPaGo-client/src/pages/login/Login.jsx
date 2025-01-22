import SocialLoginButton from '../../components/button/SocialLoginButton';
import { SOCIAL_BUTTON_STYLES } from '../../common/styles/SocialButton';

const API_HOST = import.meta.env.VITE_HOST;

function Login() {
  const handleLoginClick = (provider) => {
    window.location.href = `${API_HOST}/api/oauth2/authorization/${provider}`;
  };

  return (
    <div className="bg-white shadow-md mx-auto min-h-screen flex flex-col items-center justify-center px-4 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <img src="/logo.png" alt="Logo" className="w-32 h-auto mb-6" />
      <div className="flex flex-col items-center space-y-4 w-72">
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
