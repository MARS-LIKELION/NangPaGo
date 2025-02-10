import axiosInstance from '../api/axiosInstance';

export const createUserRecipe = async (formData) => {
  try {
    const response = await axiosInstance.post('/api/user-recipe', formData);
    return response.data;
  } catch (error) {
    console.error(`유저 레시피 생성 오류: ${error.message}`);
    throw error;
  }
};

export const deleteUserRecipe = async (id) => {
  try {
    const response = await axiosInstance.delete(`/api/user-recipe/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(
      `유저 레시피 삭제 중 오류가 발생했습니다: ${error.message}`,
    );
  }
};

export const updateUserRecipe = async (id, formData) => {
  try {
    const response = await axiosInstance.put(
      `/api/user-recipe/${id}`,
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' },
      },
    );
    return response.data;
  } catch (error) {
    console.error(`유저 레시피 수정 오류: ${error.message}`);
    throw error;
  }
};
