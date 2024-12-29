import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

function getCookie(name) {
  const matches = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

axiosInstance.interceptors.request.use((config) => {
  const accessToken = getCookie('access');
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  async (response) => {
    if (response.data?.data === '') {
      try {
        await axiosInstance.post('/api/token/reissue');
        const newAccessToken = getCookie('access');
        if (newAccessToken) {
          response.config.headers.Authorization = `Bearer ${newAccessToken}`;
          return axiosInstance(response.config);
        }
        throw new Error('Failed to retrieve new access token from cookies.');
      } catch {
        return Promise.reject();
      }
    }
    return response;
  },
  async (error) => {
    return Promise.reject(error);
  },
);

export default axiosInstance;
