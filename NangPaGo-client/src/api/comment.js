import axiosInstance from './axiosInstance';
import { COMMENT_SIZE } from '../common/constants/pagination';

export const fetchComments = (post, page) =>
  axiosInstance.get(`/api/${post.type}/${post.id}/comment`, {
    params: { pageNo: page, pageSize: COMMENT_SIZE },
  });

export const createComment = (post, commentData) =>
  axiosInstance.post(`/api/${post.type}/${post.id}/comment`, {
    userEmail: commentData.email,
    content: commentData.content,
  });

export const updateComment = (post, commentId, commentData) =>
  axiosInstance.put(
    `/api/${post.type}/${post.id}/comment/${commentId}`,
    commentData,
  );

export const deleteComment = (post, commentId) =>
  axiosInstance.delete(`/api/${post.type}/${post.id}/comment/${commentId}`);
