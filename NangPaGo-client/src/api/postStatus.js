import axiosInstance from './axiosInstance';

export const getLikeCount = async (post) => {
  try {
    const response = await axiosInstance.get(
      `/api/${post.type}/${post.id}/like/count`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 수를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchLikeStatus = async (post) => {
  try {
    const response = await axiosInstance.get(
      `/api/${post.type}/${post.id}/like/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchFavoriteStatus = async (post) => {
  try {
    const response = await axiosInstance.get(
      `/api/${post.type}/${post.id}/favorite/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleLike = async (post) => {
  try {
    const response = await axiosInstance.post(
      `/api/${post.type}/${post.id}/like/toggle`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleFavorite = async (post) => {
  try {
    const response = await axiosInstance.post(
      `/api/${post.type}/${post.id}/favorite/toggle`,
    );
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
