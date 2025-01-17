import { SOCIAL_BUTTON_STYLES } from '../util/auth.js';

function SocialLoginButton({ provider, onClick }) {
  const currentStyle = SOCIAL_BUTTON_STYLES[provider];

  return (
    <button
      onClick={onClick}
      className={`flex items-center justify-center w-72 h-12 px-3 mx-5 shadow-md ${currentStyle.background} transition`}
    >
      <img
        src={currentStyle.logo}
        alt={`${provider} logo`}
        className="h-7 w-7 mr-3"
      />

      <div className="w-3/5">
        <span className={`text-base font-medium ${currentStyle.text}`}>
          {currentStyle.label}
        </span>
      </div>
    </button>
  );
}

export default SocialLoginButton;
