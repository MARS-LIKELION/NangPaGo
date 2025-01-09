import { useLocation } from 'react-router-dom';
import { useState, useEffect, useMemo } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton';

function RecipeList() {
  const location = useLocation();

  const isLoggedIn = useMemo(
    () => localStorage.getItem('isLoggedIn') === 'true',
    [],
  );

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
        {/* 탭 컴포넌트 */}
        <RecipeListTab
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          isLoggedIn={isLoggedIn} // 로그인 상태 전달
        />

        {/* 검색 바 */}
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

        {/* 레시피 콘텐츠 */}
        <RecipeListContent
          activeTab={activeTab}
          searchTerm={searchTerm}
          isLoggedIn={isLoggedIn} // 로그인 상태 전달
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
