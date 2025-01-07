import axiosInstance from './axiosInstance';

export const getRecipes = async (ingredients, page, size) => {
  try {
    const keyword = ingredients.join(' ');
    const response = await axiosInstance.get('/api/recipe/search', {
      params: {
        pageNo: page,
        pageSize: size,
        keyword,
      },
    });
    return response.data.data;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
