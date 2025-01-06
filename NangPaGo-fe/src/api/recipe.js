import axiosInstance from './axiosInstance';

export const getRecipes = async (ingredients) => {
  try {
    const response = await axiosInstance.get('/api/recipe/search', {
      params: {
        ingredients: ingredients.join(','),
      },
    });
    return response.data.data.content;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
