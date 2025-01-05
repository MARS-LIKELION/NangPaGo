import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosInstance';

export const fetchUserStatus = createAsyncThunk(
  'login/fetchUserStatus',
  async (_, { rejectWithValue }) => {
    // API 호출 대신 localStorage만 확인
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    if (isLoggedIn) {
      return { email: localStorage.getItem('userEmail') || '' }; // 필요한 경우 이메일도 localStorage에 저장/복원
    }
    return rejectWithValue('Not logged in');
    // try {
    //   const response = await axiosInstance.get('/api/auth/status');
    //   const { data } = response.data;
    //   return data;
    // } catch (error) {
    //   return rejectWithValue(
    //     error.response?.data?.message || 'Failed to fetch user status',
    //   );
    // }
  },
);

const loadState = () => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  return { isLoggedIn };
};

const initialState = {
  ...loadState(),
  email: '',
  status: 'idle',
  error: null,
  isInitialized: false,
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
      localStorage.removeItem('isLoggedIn');
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
        localStorage.setItem('isLoggedIn', 'true');
      })
      .addCase(fetchUserStatus.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
        state.isInitialized = true;
        localStorage.removeItem('isLoggedIn');
      });
  },
});

export const { logout } = loginSlice.actions;
export default loginSlice.reducer;
