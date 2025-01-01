import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosInstance';

// 비동기 작업: 사용자 상태 가져오기
export const fetchUserStatus = createAsyncThunk(
  'login/fetchUserStatus',
  async (_, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get('/api/auth/status');
      const { data } = response.data;
      return data; // 서버에서 email 포함 데이터를 반환
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch user status',
      );
    }
  },
);

// 로컬 스토리지에서 로그인 상태 로드
const loadState = () => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  return { isLoggedIn };
};

// 초기 상태
const initialState = {
  ...loadState(), // 로컬 스토리지에서 isLoggedIn 값 불러오기
  email: '', // 민감 정보는 Redux에서만 관리
  status: 'idle',
  error: null,
  isInitialized: false, // 초기화 상태
};

const loginSlice = createSlice({
  name: 'login',
  initialState,
  reducers: {
    logout: (state) => {
      state.email = '';
      state.isLoggedIn = false;
      state.status = 'idle';
      state.error = null;
      localStorage.removeItem('isLoggedIn'); // 로그아웃 시 상태 제거
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserStatus.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(fetchUserStatus.fulfilled, (state, action) => {
        state.email = action.payload.email;
        state.isLoggedIn = true;
        state.status = 'succeeded';
        state.isInitialized = true;
        localStorage.setItem('isLoggedIn', 'true'); // 로그인 상태만 저장
      })
      .addCase(fetchUserStatus.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.isInitialized = true;
        localStorage.removeItem('isLoggedIn'); // 로그인 실패 시 상태 제거
      });
  },
});

export const { logout } = loginSlice.actions;
export default loginSlice.reducer;
