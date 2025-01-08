import { useLocation, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';
import Footer from '../../components/common/Footer.jsx';
import TopButton from '../../components/common/TopButton';
import CreateButton from '../../components/common/CreateButton.jsx';
import axiosInstance from '../../api/axiosInstance';

function RecipeList() {
  const location = useLocation();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('recommended');
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const [searchTerm, setSearchTerm] = useState(
    location.state?.searchTerm || '',
  );
  const [recipes, setRecipes] = useState([]);

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

  useEffect(() => {
    if (activeTab === 'favorites') {
      const fetchFavoriteRecipes = async () => {
        try {
          const response = await axiosInstance.get('/api/recipe/favorite/list');
          console.log(response);
          setRecipes(response.data.data || []);
        } catch (error) {
          console.error(error);
          setRecipes([]);
        }
      };

      fetchFavoriteRecipes();
    }
  }, [activeTab]);

  const handleCreateClick = () => {
    alert('글 생성 버튼이 클릭되었습니다!');
    // 실제로는 navigate('/recipe/create') 등으로 이동할 수도 있음
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />

      <div className="flex-grow px-4 space-y-4">
        <RecipeListTab activeTab={activeTab} setActiveTab={setActiveTab} />

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

        {activeTab !== 'favorites' && (
          <RecipeListContent activeTab={activeTab} searchTerm={searchTerm} />
        )}

        {activeTab === 'favorites' && recipes.length === 0 ? (
          <div className="flex items-center justify-center text-gray-500 text-center py-10">
            즐겨찾기한 레시피가 없습니다.
          </div>
        ) : (
          activeTab === 'favorites' && (
            <RecipeListContent activeTab={activeTab} recipes={recipes} />
          )
        )}
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
