import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosInstance';

// access 토큰이 쿠키에 있는지 확인하는 함수
const hasAccessToken = () => {
  return document.cookie
    .split('; ')
    .some(row => row.startsWith('access='));
};

export const fetchUserStatus = createAsyncThunk(
  'login/fetchUserStatus',
  async (_, { rejectWithValue }) => {
    // access 토큰이 없으면 API 호출하지 않고 바로 종료
    if (!hasAccessToken()) {
      return rejectWithValue('No access token found');
    }
    
    try {
      const response = await axiosInstance.get('/api/auth/status');
      const { data } = response.data;
      return data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch user status',
      );
    }
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
