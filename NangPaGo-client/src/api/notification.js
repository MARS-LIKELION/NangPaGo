import axiosInstance from './axiosInstance';

export async function getNotificationList() {
  try {
    const response = await axiosInstance.get('/api/user/notification/list');
    return response.data.data;
  } catch (error) {
    console.error('알림 목록 조회 실패:', error);
    throw error;
  }
}
