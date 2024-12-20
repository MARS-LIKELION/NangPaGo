import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './globals.css';
import Router from './pages/index.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Router />
  </StrictMode>,
);
