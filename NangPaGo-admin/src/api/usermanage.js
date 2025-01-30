import axiosInstance from './axiosInstance';

export const getUserList = async (page) => {
  try {
    const response = await axiosInstance.get(`/api/user?page=${page}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `관리자 페이지를 불러오는 중 에러가 발생했습니다.: ${error.message}`,
    );
  }
};

export const banUser = async (userId) => {
  try {
    const response = await axiosInstance.put(`/api/user/ban?userId=${userId}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `차단 중 에러가 발생했습니다.: ${error.message}`,
    );
  }
};

export const unBanUser = async (userId) => {
  try {
    const response = await axiosInstance.put(`/api/user/unban?userId=${userId}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `차단 해제 중 에러가 발생했습니다.: ${error.message}`,
    );
  }
};
