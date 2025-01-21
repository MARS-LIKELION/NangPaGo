import { useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/layout/header/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton';

function RecipeList() {
  const location = useLocation();
  const isLoggedIn = useSelector((state) => state.loginSlice.isLoggedIn);

  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState(
    location.state?.searchTerm || '',
  );
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);

  const handleClearSearch = () => {
    setSearchTerm('');
  };

  useEffect(() => {
    const handleScroll = () => {
      setIsTopButtonVisible(window.scrollY > 100);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div className="bg-white shadow-md mx-auto min-h-screen flex flex-col min-w-80 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header />

      <main className="flex-grow px-4 space-y-4">
        <RecipeListTab
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          isLoggedIn={isLoggedIn}
        />

        {activeTab !== 'favorites' && (
          <div className="flex justify-center md:justify-start">
            <SearchBar
              searchPath={'/recipe/search'}
              searchTerm={searchTerm}
              onClear={handleClearSearch}
              className="w-full max-w-xs md:max-w-md lg:max-w-lg"
            />
          </div>
        )}
        <RecipeListContent
          activeTab={activeTab}
          searchTerm={searchTerm}
          isLoggedIn={isLoggedIn}
        />
      </main>
      <aside className="w-full my-0 mx-auto fixed z-50 left-0 right-0 bottom-0 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
        <div>{isTopButtonVisible && <TopButton />}</div>
      </aside>
      <Footer />
    </div>
  );
}

export default RecipeList;
