import axiosInstance from './axiosInstance';

export const fetchRecipeById = async (recipeId) => {
  try {
    const response = await axiosInstance.get(`/api/recipe/${recipeId}`);
    return response.data.data;
  } catch (error) {
    console.error('레시피를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const getRecipes = async (ingredients, page, size) => {
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

export const searchRecipes = async (
  keyword,
  pageNo = 1,
  pageSize = 10,
  searchType = 'NAME',
) => {
  try {
    const response = await axiosInstance.get('/api/recipe/search', {
      params: { pageNo, pageSize, keyword, searchType },
    });
    return response.data.data.content;
  } catch (error) {
    console.error('레시피 검색 요청 실패:', error);
    return [];
  }
};

export const fetchRecommendedRecipes = async (
  searchTerm,
  pageNo = 1,
  pageSize = 12,
) => {
  try {
    const params = {
      pageNo,
      pageSize,
      ...(searchTerm && { keyword: searchTerm, searchType: 'NAME' }),
    };
    const response = await axiosInstance.get('/api/recipe/search', { params });

    const { content, last, number } = response.data.data;
    return { content: content || [], last, number };
  } catch (error) {
    console.error('Error fetching recommended recipes:', error);
    return { content: [], last: true, number: pageNo };
  }
};

export const fetchFavoriteRecipes = async (page, size) => {
  try {
    const params = {
      pageNo: page,
      pageSize: size,
    };
    const response = await axiosInstance.get('/api/recipe/favorite/list', { params });
    const { content, last, number } = response.data.data;
    return { content: content || [], last, number };
  } catch (error) {
    console.error('즐겨찾기한 레시피 목록 조회 실패:', error);
    return { content: [], last: true, number: page };
  }
};


export const getLikeCount = async (recipeId) => {
  try {
    const response = await axiosInstance.get(
      `/api/recipe/${recipeId}/like/count`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 수를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchLikeStatus = async (recipeId) => {
  try {
    const response = await axiosInstance.get(
      `/api/recipe/${recipeId}/like/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const fetchFavoriteStatus = async (recipeId) => {
  try {
    const response = await axiosInstance.get(
      `/api/recipe/${recipeId}/favorite/status`,
    );
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 가져오는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleLike = async (recipeId) => {
  try {
    const response = await axiosInstance.post(
      `/api/recipe/${recipeId}/like/toggle`,
    );
    return response.data.data;
  } catch (error) {
    console.error('좋아요 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};

export const toggleFavorite = async (recipeId) => {
  try {
    const response = await axiosInstance.post(
      `/api/recipe/${recipeId}/favorite/toggle`,
    );
    return response.data.data;
  } catch (error) {
    console.error('즐겨찾기 상태를 변경하는 중 오류가 발생했습니다:', error);
    throw error;
  }
};
