import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('access');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => {
    console.log(response.headers);
    const accessToken = response.headers['access'];
    if (accessToken) {
      localStorage.setItem('access', accessToken);
    }
    return response;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default axiosInstance;
