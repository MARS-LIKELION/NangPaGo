import axiosInstance from './axiosInstance';

export const fetchComments = (recipeId, pageNo = 0, pageSize = 5) =>
  axiosInstance.get(`/api/recipe/${recipeId}/comment`, {
    params: { pageNo, pageSize },
  });

export const createComment = (recipeId, commentData) => {
  return axiosInstance.post(`/api/recipe/${recipeId}/comment`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (recipeId, commentId, commentData) =>
  axiosInstance.put(
    `/api/recipe/${recipeId}/comment/${commentId}`,
    commentData,
  );

export const deleteComment = (recipeId, commentId) =>
  axiosInstance.delete(`/api/recipe/${recipeId}/comment/${commentId}`);
