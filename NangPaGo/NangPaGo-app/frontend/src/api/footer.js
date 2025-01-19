import { useState } from 'react';
import axiosInstance from './axiosInstance';

const versionFetcher = () => {
  const [version, setVersionInfo] = useState({
    fetchState: 'idle',
    message: null,
    version: null,
  });

  const fetchVersion = async () => {
    setVersionInfo({ fetchState: 'loading', message: null, version: null });
    try {
      const response = await axiosInstance.get(`/api/common/version`);
      setVersionInfo({
        fetchState: 'success',
        message: null,
        version: response.data,
      });
    } catch (error) {
      setVersionInfo({
        fetchState: 'error',
        message: '버전 정보를 가져오는 데 실패했습니다.',
        version: null,
      });
    }
  };

  return { version, fetchVersion };
};

export default versionFetcher;
