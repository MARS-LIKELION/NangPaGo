import axiosInstance from './axiosInstance';

export const fetchPostById = async (post) => {
  try {
    const response = await axiosInstance.get(`/api/${post.type}/${post.id}`);
    return response.data;
  } catch (error) {
    console.error(
      `${post.type === 'recipe' ? '레시피' : '커뮤니티'} 게시물을 가져오는 중 오류가 발생했습니다:`,
      error,
    );
    throw error;
  }
};
