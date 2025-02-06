import axiosInstance from './axiosInstance';

export const getAuditLogs = async (page, pageSize = 10) => {
  try {
    const params = new URLSearchParams();
    params.append('pageNo', page + 1);
    params.append('pageSize', pageSize);

    const response = await axiosInstance.get(`/api/audit?${params.toString()}`);
    return response;
  } catch (error) {
    throw error;
  }
};
