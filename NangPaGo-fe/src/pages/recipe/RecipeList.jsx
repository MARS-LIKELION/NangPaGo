import { useState } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';

function RecipeList() {
  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
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
        <RecipeListContent
          activeTab={activeTab}
        />
      </div>
      <Footer />
    </div>
  );
}

export default RecipeList;
