import axiosInstance from './axiosInstance';
import { PAGE_INDEX, PAGE_SIZE } from '../common/constants/pagination';

export const fetchPosts = async (
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.search,
) => {
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
    if (error.response?.data?.message) {
      throw new Error(`${error.response.data.message}`);
    }

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
    if (error.response?.data?.message) {
      throw new Error(`${error.response.data.message}`);
    }
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
