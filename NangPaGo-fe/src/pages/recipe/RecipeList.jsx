import { useLocation } from 'react-router-dom';
import { useState } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton'; // 모듈화한 TopButton 컴포넌트 추가

function RecipeList() {
  const location = useLocation();
  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState('');
  const recipes = location.state?.recipes || [];

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen relative">
      <Header />
      <div className="px-4 space-y-4">
        <RecipeListTab activeTab={activeTab} setActiveTab={setActiveTab} />
        <div className="flex justify-center">
          <SearchBar
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            className="w-[200px]"
          />
        </div>
        <RecipeListContent activeTab={activeTab} recipes={recipes} />
      </div>
      <Footer />

      <TopButton
        offset={100}
        positionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
      />
    </div>
  );
}

export default RecipeList;
