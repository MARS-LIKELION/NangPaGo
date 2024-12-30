import axiosInstance from './axiosInstance';

export const fetchComments = (recipeId, pageNo = 0, pageSize = 5) =>
  axiosInstance.get(`/api/recipe/${recipeId}/comments`, {
    params: { pageNo, pageSize },
  });

export const createComment = (recipeId, commentData) => {
  return axiosInstance.post(`/api/recipe/${recipeId}/comments`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (recipeId, commentId, commentData) =>
  axiosInstance.put(
    `/api/recipe/${recipeId}/comments/${commentId}`,
    commentData,
  );

export const deleteComment = (recipeId, commentId) =>
  axiosInstance.delete(`/api/recipe/${recipeId}/comments/${commentId}`);
