import { configureStore } from '@reduxjs/toolkit';
import loginSlice from './slices/loginSlice.js';

export default configureStore({
  reducer: {
    loginSlice: loginSlice,
  },
});
