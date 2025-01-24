import axiosInstance from './axiosInstance';

export const getUserInfos = async (page) => {
  try {
    const response = await axiosInstance.get(`/api/dashboard/users?page=${page}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `관리자 페이지를 불러오는 중 에러가 발생했습니다.: ${error.message}`,
    );
  }
};
