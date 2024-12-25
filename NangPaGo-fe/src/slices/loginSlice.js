import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  email: '',
};

const loginSlice = createSlice({
  name: 'loginSlice',
  initialState: initialState,
  reducers: {
    login: (state, action) => {
      return { email: action.payload.email };
    },
    logout: () => {
      console.log('logout');
      return { ...initialState };
    },
  },
});

export const { login, logout } = loginSlice.actions;

export default loginSlice.reducer;
