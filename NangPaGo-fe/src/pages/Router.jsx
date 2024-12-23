import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from './login/LoginPage';
import RecipeList from './recipe/RecipeList';
import SearchPage from './search/SearchPage';

const Router = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RecipeList />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/search" element={<SearchPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default Router;
