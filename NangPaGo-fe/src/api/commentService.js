import axiosInstance from './axiosInstance';

export const fetchComments = (recipeId) =>
  axiosInstance.get(`/recipe/${recipeId}/comments`);

export const createComment = (recipeId, commentData) => {
  return axiosInstance.post(`/recipe/${recipeId}/comments`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (recipeId, commentId, commentData) =>
  axiosInstance.put(`/recipe/${recipeId}/comments/${commentId}`, commentData);

export const deleteComment = (recipeId, commentId) =>
  axiosInstance.delete(`/recipe/${recipeId}/comments/${commentId}`);
