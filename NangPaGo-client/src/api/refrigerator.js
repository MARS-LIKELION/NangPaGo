import axiosInstance from './axiosInstance';

export async function getRefrigerator(page, size) {
  try {
    const response = await axiosInstance.get('/api/refrigerator', {
      params: { page, size },
    });
    return response.data.data;
  } catch (error) {
    console.error('냉장고 조회 실패:', error);
    throw error;
  }
}

export async function addIngredient(ingredientName) {
  try {
    const response = await axiosInstance.post('/api/refrigerator', null, {
      params: { ingredientName },
    });
    return response.data.data;
  } catch (error) {
    console.error('재료 추가 실패:', error);
    throw error;
  }
}

export async function searchIngredients(keyword) {
  try {
    const response = await axiosInstance.get('/api/ingredient/search', {
      params: { keyword },
    });
    return response.data.data;
  } catch (error) {
    console.error('재료 검색 실패:', error);
    throw error;
  }
}

export async function deleteIngredient(ingredientName) {
  try {
    const response = await axiosInstance.delete('/api/refrigerator', {
      params: { ingredientName },
    });
    return response.data.message;
  } catch (error) {
    console.error('재료 삭제 실패:', error);
    throw error;
  }
}

export const searchPostsByIngredient = async (ingredients, page, size) => {
  try {
    const keyword = ingredients.join(' ');
    const response = await axiosInstance.get('/api/recipe/search', {
      params: { pageNo: page, pageSize: size, keyword },
    });
    return response.data.data;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
