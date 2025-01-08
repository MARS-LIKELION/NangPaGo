import axiosInstance from './axiosInstance';

export const fetchComments = (id, pageNo = 0, pageSize = 5) =>
  axiosInstance.get(`/api/community/${id}/comments`, {
    params: { pageNo, pageSize },
  });

export const createComment = (id, commentData) => {
  return axiosInstance.post(`/api/community/${id}/comments`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (id, commentId, commentData) =>
  axiosInstance.put(
    `/api/community/${id}/comments/${commentId}`,
    commentData,
  );

export const deleteComment = (id, commentId) =>
  axiosInstance.delete(`/api/community/${id}/comments/${commentId}`);

export const fetchCommentCount = (id) =>
  axiosInstance
    .get(`/api/community/${id}/comments/count`)
    .then((response) => response.data.data);
