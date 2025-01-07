import { useLocation, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton';
import CreateButton from '../../components/common/CreateButton';

function RecipeList() {
  const location = useLocation();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('recommended');
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const [searchTerm, setSearchTerm] = useState(location.state?.searchTerm || '');

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

  const handleCreateClick = () => {
    alert('글 생성 버튼이 클릭되었습니다!');
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen relative">
      <Header />
      <div className="px-4 space-y-4">
        <RecipeListTab activeTab={activeTab} setActiveTab={setActiveTab} />
        <div className="flex justify-center">
          <SearchBar
            searchPath={'/recipe/search'}
            searchTerm={searchTerm}
            onClear={handleClearSearch}
            className="w-[200px]"
          />
        </div>
        <RecipeListContent activeTab={activeTab} searchTerm={searchTerm} />
      </div>
      <Footer />
      <CreateButton
        onClick={handleCreateClick}
        isTopButtonVisible={isTopButtonVisible}
        basePositionClass="bottom-10 right-[calc((100vw-375px)/2+16px)]"
      />
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
