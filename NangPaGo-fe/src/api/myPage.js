import axiosInstance from './axiosInstance';

export async function getMyPageInfo() {
  try {
    const response = await axiosInstance.get('/api/user/my-page');
    console.log('마이페이지 정보:', response.data.data);
    return response.data.data;
  } catch (error) {
    console.error('마이페이지 정보 조회 실패:', error);
    throw error;
  }
}

export async function getComments(page, size) {
  try {
    const response = await axiosInstance.get('/api/user/comments', {
      params: { pageNo: page, pageSize: size },
    });
    return response.data.data;
  } catch (error) {
    console.error('댓글 단 레시피 목록 조회 실패:', error);
    throw error;
  }
}

export async function getLikes(page, size) {
  try {
    const response = await axiosInstance.get('/api/user/likes/recipes', {
      params: { pageNo: page, pageSize: size },
    });
    return response.data.data;
  } catch (error) {
    console.error('좋아요한 레시피 목록 조회 실패:', error);
    throw error;
  }
}

export async function getFavorites(page, size) {
  try {
    const response = await axiosInstance.get('/api/user/favorites/recipes', {
      params: { pageNo: page, pageSize: size },
    });
    console.log('즐겨찾기한 레시피 목록:', response.data.data);
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기한 레시피 목록 조회 실패:', error);
    throw error;
  }
}
