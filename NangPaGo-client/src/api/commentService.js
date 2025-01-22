import axiosInstance from './axiosInstance';

export const fetchComments = (entityType, entityId, pageNo = 0, pageSize = 5) =>
  axiosInstance.get(`/api/${entityType}/${entityId}/comment`, {
    params: { pageNo, pageSize },
  });

export const createComment = (entityType, entityId, commentData) => {
  return axiosInstance.post(`/api/${entityType}/${entityId}/comment`, {
    userEmail: commentData.email,
    content: commentData.content,
  });
};

export const updateComment = (entityType, entityId, commentId, commentData) =>
  axiosInstance.put(
    `/api/${entityType}/${entityId}/comment/${commentId}`,
    commentData,
  );

export const deleteComment = (entityType, entityId, commentId) =>
  axiosInstance.delete(`/api/${entityType}/${entityId}/comment/${commentId}`);
