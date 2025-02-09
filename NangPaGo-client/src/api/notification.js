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

export async function markNotificationsAsRead() {
  try {
    const response = await axiosInstance.put('/api/user/notification/state/read');
    return response.data.data;
  } catch (error) {
    console.error('알림 읽음 처리 실패:', error);
    throw error;
  }
}
