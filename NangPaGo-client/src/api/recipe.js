import axiosInstance from './axiosInstance';
import { PAGE_INDEX, PAGE_SIZE } from '../common/constants/pagination';

export const searchPostsByKeyword = async (
  keyword,
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.search,
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

export const fetchRecommendedPosts = async (
  searchTerm,
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.list,
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

export const fetchFavoritePosts = async (page, size) => {
  try {
    const params = {
      pageNo: page,
      pageSize: size,
    };
    const response = await axiosInstance.get('/api/recipe/favorite/list', {
      params,
    });
    const { content, last, number } = response.data.data;
    return { content: content || [], last, number };
  } catch (error) {
    console.error('즐겨찾기한 레시피 목록 조회 실패:', error);
    return { content: [], last: true, number: page };
  }
};
