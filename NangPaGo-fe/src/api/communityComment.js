import axiosInstance from './axiosInstance';

export const fetchComments = (id, pageNo = 0, pageSize = 5) =>
  axiosInstance.get(`/api/community/${id}/comments`, {
    params: { pageNo, pageSize },
  });

export const createComment = (id, commentData) => {
  return axiosInstance.post(`/api/community/${id}/comment`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (id, commentId, commentData) =>
  axiosInstance.put(`/api/community/${id}/comment/${commentId}`, commentData);

export const deleteComment = (id, commentId) =>
  axiosInstance.delete(`/api/community/${id}/comment/${commentId}`);

export const fetchCommentCount = (id) =>
  axiosInstance
    .get(`/api/community/${id}/comment/count`)
    .then((response) => response.data.data);
