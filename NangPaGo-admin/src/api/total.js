import axiosInstance from './axiosInstance';

export const getDashboardData = async (months = 12) => {
  try {
    const response = await axiosInstance.get(`/api/dashboard?months=${months}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `대시보드 데이터를 불러오는 중 에러가 발생했습니다: ${error.message}`
    );
  }
};
