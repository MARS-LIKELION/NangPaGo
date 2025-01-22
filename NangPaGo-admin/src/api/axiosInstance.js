import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_HOST,
  withCredentials: true,
});
export default axiosInstance;
