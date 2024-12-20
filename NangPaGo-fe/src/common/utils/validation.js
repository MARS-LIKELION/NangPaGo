import { VALID_PROVIDERS } from '../constants/providers';

export const isValidProvider = (provider) => {
  return VALID_PROVIDERS.includes(provider);
};
