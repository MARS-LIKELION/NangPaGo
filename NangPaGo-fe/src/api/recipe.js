import axiosInstance from './axiosInstance';

export const getRecipes = async (ingredients) => {
  try {
    const keyword = ingredients.join(', ');
    console.log('전송할 keyword:', keyword);
    const response = await axiosInstance.get('/api/recipe/search', {
      params: {
        pageNo: 1,
        pageSize: 10,
        keyword,
      },
    });
    console.log('받은 데이터:', response.data.data.content);
    return response.data.data.content;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
