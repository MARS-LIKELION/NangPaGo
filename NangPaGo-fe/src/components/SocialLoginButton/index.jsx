import React from 'react';
import SOCIAL_BUTTON_STYLES from './styles';
import { getStyleForProvider } from './utils';

function SocialLoginButton({ provider, onClick }) {
  const currentStyle = getStyleForProvider(SOCIAL_BUTTON_STYLES, provider);

  if (!currentStyle) {
    return null;
  }

  return (
    <button
      onClick={onClick}
      className={`flex items-center justify-center w-full h-12 px-4 mx-5 rounded-lg shadow ${currentStyle.background} transition`}
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
