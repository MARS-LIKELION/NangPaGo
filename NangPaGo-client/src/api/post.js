import axiosInstance from './axiosInstance';
import { PAGE_INDEX, PAGE_SIZE } from '../common/constants/pagination';

export const fetchPostById = async (post) => {
  try {
    const response = await axiosInstance.get(`/api/${post.type}/${post.id}`);
    return response.data;
  } catch (error) {
    console.error(
      `${post.type === 'recipe' ? '레시피' : post.type === 'user-recipe' ? '유저 레시피' : '커뮤니티'} 게시물을 가져오는 중 오류가 발생했습니다: `,
      error,
    );
    throw error;
  }
};

export const fetchPostList = async (
  postType,
  pageNo = PAGE_INDEX.one,
  pageSize = PAGE_SIZE.search,
) => {
  try {
    const response = await axiosInstance.get(`/api/${postType}/list`, {
      params: { pageNo, pageSize },
    });
    return response.data;
  } catch (error) {
    throw new Error(
      `${post.type === 'user-recipe' ? '유저 레시피' : '커뮤니티'} 목록을 가져오는 중 오류가 발생했습니다: `,
      error,
    );
  }
};
