import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from './login/LoginPage';
import RecipeList from './recipe/RecipeList';

const Router = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RecipeList />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default Router;
