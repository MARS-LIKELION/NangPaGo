import { useLocation, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton';
import CreateButton from '../../components/common/CreateButton.jsx';

function RecipeList() {
  const location = useLocation();
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const initialTab = queryParams.get('tab') || 'recommended';

  const [activeTab, setActiveTab] = useState(initialTab);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const [searchTerm, setSearchTerm] = useState(
    location.state?.searchTerm || '',
  );

  const handleTabChange = (newTab) => {
    setActiveTab(newTab);
    navigate(`?tab=${newTab}`, { replace: true });
  };

  useEffect(() => {
    const handleScroll = () => {
      setIsTopButtonVisible(window.scrollY > 100);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleCreateClick = () => {
    alert('글 생성 버튼이 클릭되었습니다!');
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />

      <div className="flex-grow px-4 space-y-4">
        <RecipeListTab activeTab={activeTab} setActiveTab={handleTabChange} />

        {activeTab !== 'favorites' && (
          <div className="flex justify-center">
            <SearchBar
              searchPath={'/recipe/search'}
              searchTerm={searchTerm}
              onClear={() => setSearchTerm('')}
              className="w-[200px]"
            />
          </div>
        )}

        <RecipeListContent activeTab={activeTab} searchTerm={searchTerm} />
      </div>

      <Footer />

      {activeTab === 'recommended' && (
        <CreateButton
          onClick={handleCreateClick}
          isTopButtonVisible={isTopButtonVisible}
          basePositionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
        />
      )}

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
