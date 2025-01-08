import axiosInstance from './axiosInstance';

export const fetchCommunityList = (pageNo = 0, pageSize = 10) => {
  return axiosInstance
    .get('/api/community/list', {
      params: { pageNo, pageSize },
    })
    .then((response) => response.data);
};

export const createCommunity = (data) => {
  return axiosInstance
    .post('/api/community', data)
    .then((response) => response.data);
};

export const updateCommunity = (id, data) => {
  return axiosInstance
    .put(`/api/community/${id}`, data)
    .then((response) => response.data);
};

// 게시물 삭제
export const deleteCommunity = (id) => {
  return axiosInstance
    .delete(`/api/community/${id}`)
    .then((response) => response.data);
};
