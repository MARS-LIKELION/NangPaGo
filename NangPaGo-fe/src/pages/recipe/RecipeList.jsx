import { useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
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
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />

      <div className="flex-grow px-4 space-y-4">
        <RecipeListTab
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          isLoggedIn={isLoggedIn}
        />

        {activeTab !== 'favorites' && (
          <div className="flex justify-center">
            <SearchBar
              searchPath={'/recipe/search'}
              searchTerm={searchTerm}
              onClear={handleClearSearch}
              className="w-[200px]"
            />
          </div>
        )}

        <RecipeListContent
          activeTab={activeTab}
          searchTerm={searchTerm}
          isLoggedIn={isLoggedIn}
        />
      </div>

      <Footer />

      {isTopButtonVisible && (
        <TopButton
          offset={100}
          positionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
        />
      )}
    </div>
  );
}

export default RecipeList;
