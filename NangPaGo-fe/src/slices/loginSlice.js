import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../api/axiosInstance';

export const fetchUserStatus = createAsyncThunk(
  'login/fetchUserStatus',
  async (_, { rejectWithValue }) => {
    try {
      console.log('Fetching user status...');
      const response = await axiosInstance.get('/auth/status');
      console.log('User status response:', response);

      const { data } = response.data;
      return data;
    } catch (error) {
      console.error('Error fetching user status:', error.response || error);
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch user status',
      );
    }
  },
);

const loginSlice = createSlice({
  name: 'login',
  initialState: {
    email: '',
    isLoggedIn: false,
    status: 'idle',
    error: null,
  },
  reducers: {
    logout: (state) => {
      state.email = '';
      state.isLoggedIn = false;
      state.status = 'idle';
      state.error = null;
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
      })
      .addCase(fetchUserStatus.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      });
  },
});

export const { logout } = loginSlice.actions;

export default loginSlice.reducer;
