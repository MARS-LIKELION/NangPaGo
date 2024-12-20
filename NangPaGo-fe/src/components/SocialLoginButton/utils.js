export const getStyleForProvider = (styles, provider) => {
  if (!styles[provider]) {
    throw new Error(`알 수 없는 제공자: ${provider}`);
  }
  return styles[provider];
};
