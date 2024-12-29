import axiosInstance from './axiosInstance';

export const fetchComments = (recipeId) =>
  axiosInstance.get(`/recipe/${recipeId}/comments`);

export const createComment = (recipeId, commentData) =>
  axiosInstance.post(`/recipe/${recipeId}/comments`, commentData);

export const updateComment = (recipeId, commentId, commentData) =>
  axiosInstance.put(`/recipe/${recipeId}/comments/${commentId}`, commentData);

export const deleteComment = (recipeId, commentId) =>
  axiosInstance.delete(`/recipe/${recipeId}/comments/${commentId}`);
