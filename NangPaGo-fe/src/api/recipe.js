import axiosInstance from './axiosInstance'; // 프로젝트에서 axios 인스턴스를 관리 중이라면

export const getRecipes = async (ingredients) => {
  try {
    const response = await axiosInstance.get('/api/recipe/search', {
      params: {
        ingredients: ingredients.join(','), // 쿼리 매개변수로 전달
      },
    });
    return response.data.data.content; // API 응답에서 필요한 데이터 반환
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
