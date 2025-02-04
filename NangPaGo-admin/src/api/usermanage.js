import axiosInstance from './axiosInstance';



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

export const getUserList = async (page, sort, status, provider) => {
  try {
    const params = new URLSearchParams();
    
    params.append('pageNo', page);
    params.append('sort', sort);
    
    if (status) {
      params.append('status', status);
    }
    
    if (provider) {
      params.append('provider', provider);
    }
    
    const response = await axiosInstance.get(`/api/user?${params.toString()}`);
    return response;
  } catch (error) {
    throw error;
  }
};
