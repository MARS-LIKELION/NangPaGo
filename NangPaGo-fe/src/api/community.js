import axiosInstance from './axiosInstance';

export const fetchCommunityList = async (pageNo = 0, pageSize = 10) => {
  try {
    const response = await axiosInstance.get('/api/community/list', {
      params: { pageNo, pageSize },
    });
    return response.data;
  } catch (error) {
    throw new Error(
      `커뮤니티 목록을 가져오는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const getCommunityDetail = async (id) => {
  try {
    const response = await axiosInstance.get(`/api/community/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `커뮤니티 세부 정보를 가져오는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const createCommunity = async (data, file) => {
  const formData = new FormData();
  formData.append('title', data.title);
  formData.append('content', data.content);
  formData.append('isPublic', data.isPublic);
  if (file) formData.append('file', file);

  try {
    const response = await axiosInstance.post('/api/community', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  } catch (error) {
    throw new Error(
      `커뮤니티를 생성하는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const updateCommunity = async (id, data) => {
  try {
    const response = await axiosInstance.put(`/api/community/${id}`, data);
    return response.data;
  } catch (error) {
    throw new Error(
      `커뮤니티를 수정하는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const deleteCommunity = async (id) => {
  try {
    const response = await axiosInstance.delete(`/api/community/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `커뮤니티를 삭제하는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const getLikeStatus = async (id) => {
  try {
    const response = await axiosInstance.get(
      `/api/community/${id}/like/status`,
    );
    return response.data.data;
  } catch (error) {
    throw new Error(
      `좋아요 상태를 가져오는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const getLikeCount = async (id) => {
  try {
    const response = await axiosInstance.get(`/api/community/${id}/like/count`);
    return response.data.data;
  } catch (error) {
    throw new Error(
      `좋아요 수를 가져오는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const toggleLike = async (id) => {
  try {
    const response = await axiosInstance.post(
      `/api/community/${id}/like/toggle`,
    );
    return response.data.data.liked;
  } catch (error) {
    throw new Error(
      `좋아요 상태를 변경하는 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};
