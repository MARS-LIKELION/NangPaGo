import React from 'react';
import ReactDOM from 'react-dom/client';
import './globals.css';
import App from './App.jsx';
import { Provider } from 'react-redux';
import store from './store';

const root = document.getElementById('root');

ReactDOM.createRoot(root).render(
  <Provider store={store}>
    <App />
  </Provider>,
);
