import axiosInstance from './axiosInstance';
import { PAGE_INDEX, PAGE_SIZE } from '../common/constants/pagination'

export const fetchDetailById = async (entityType, entityId) => {
  try {
    const response = await axiosInstance.get(`/api/${entityType}/${entityId}`);
    return response.data.data;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const getDetails = async (ingredients, page, size) => {
  try {
    const keyword = ingredients.join(' ');
    const response = await axiosInstance.get('/api/${entityType}/search', {
      params: { pageNo: page, pageSize: size, keyword },
    });
    return response.data.data;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const searchDetails = async (
  keyword,
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.search,
  searchType = 'NAME',
) => {
  try {
    const response = await axiosInstance.get('/api/${entityType}/search', {
      params: { pageNo, pageSize, keyword, searchType },
    });
    return response.data.data.content;
  } catch (error) {
    console.error('레시피 검색 요청 실패:', error);
    return [];
  }
};

export const fetchRecommendedDetails = async (
  searchTerm,
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.list,
) => {
  try {
    const params = {
      pageNo,
      pageSize,
      ...(searchTerm && { keyword: searchTerm, searchType: 'NAME' }),
    };
    const response = await axiosInstance.get('/api/${entityType}/search', { params });

    const { content, last, number } = response.data.data;
    return { content: content || [], last, number };
  } catch (error) {
    console.error('Error fetching recommended recipes:', error);
    return { content: [], last: true, number: pageNo };
  }
};

export const fetchFavoriteDetails = async (page, size) => {
  try {
    const params = {
      pageNo: page,
      pageSize: size,
    };
    const response = await axiosInstance.get('/api/${entityType}/favorite/list', { params });
    const { content, last, number } = response.data.data;
    return { content: content || [], last, number };
  } catch (error) {
    console.error('즐겨찾기한 레시피 목록 조회 실패:', error);
    return { content: [], last: true, number: page };
  }
};


export const getLikeCount = async (entityId) => {
  try {
    const response = await axiosInstance.get(
      `/api/${entityType}/${entityId}/like/count`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 수를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchLikeStatus = async (entityId) => {
  try {
    const response = await axiosInstance.get(
      `/api/${entityType}/${entityId}/like/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchFavoriteStatus = async (entityId) => {
  try {
    const response = await axiosInstance.get(
      `/api/${entityType}/${entityId}/favorite/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleLike = async (entityId) => {
  try {
    const response = await axiosInstance.post(
      `/api/${entityType}/${entityId}/like/toggle`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleFavorite = async (entityId) => {
  try {
    const response = await axiosInstance.post(
      `/api/${entityType}/${entityId}/favorite/toggle`,
    );
    return response.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
