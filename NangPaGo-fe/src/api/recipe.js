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

export const fetchRecommendedRecipes = async (
  searchTerm,
  pageNo = 1,
  pageSize = 10,
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

export const fetchFavoriteRecipes = async (
  page = 0,
  size = 10,
  sort = 'createdAt,desc',
) => {
  try {
    const response = await axiosInstance.get('/api/recipe/favorite/list', {
      params: { page, size, sort },
    });
    return response.data.data;
  } catch (error) {
    console.error('Error fetching favorite recipes:', error);
    return {
      content: [],
      currentPage: 0,
      totalPages: 0,
      totalItems: 0,
      isLast: true,
    };
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
