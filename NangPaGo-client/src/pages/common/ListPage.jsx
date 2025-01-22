import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import TopButton from '../../components/button/TopButton';
import CreateButton from '../../components/button/CreateButton';
import { PAGE_STYLES, BUTTON_STYLES } from '../../common/styles/ListPage';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import CommunityListContent from '../../components/community/CommunityListContent';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import SearchBar from '../../components/search/SearchBar';

const contentComponents = {
  recipe: RecipeListContent,
  community: CommunityListContent,
};

function ListPage({ type }) {
  const location = useLocation();
  const navigate = useNavigate();
  const isLoggedIn = useSelector((state) => state.loginSlice.isLoggedIn);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState(location.state?.searchTerm || '');

  useEffect(() => {
    const handleScroll = () => setIsTopButtonVisible(window.scrollY > 100);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleClearSearch = () => setSearchTerm('');

  const renderHeader = () => {
    if (type === 'recipe') {
      return (
        <>
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
        </>
      );
    }
    return <div className={PAGE_STYLES.header}>커뮤니티</div>;
  };

  const renderButton = () => {
    if (type === 'recipe') {
      return (
        <aside className={BUTTON_STYLES.wrapper}>
          {isTopButtonVisible && <TopButton />}
        </aside>
      );
    }
    return (
      <aside className={BUTTON_STYLES.wrapper}>
        <div className={BUTTON_STYLES.body}>
          <CreateButton
            onClick={() => {
              navigate('/community/new', { state: { from: window.location.pathname } });
            }}
            isTopButtonVisible={isTopButtonVisible}
          />
          {isTopButtonVisible && <TopButton />}
        </div>
      </aside>
    );
  };

  const ContentComponent = contentComponents[type];

  return (
    <div className={PAGE_STYLES.wrapper}>
      <Header />
      <main className={PAGE_STYLES.body}>
        {renderHeader()}
        <ContentComponent
          activeTab={activeTab}
          searchTerm={searchTerm}
          {...(type === 'recipe' ? { isLoggedIn } : {})}
        />
      </main>
      {renderButton()}
      <Footer />
    </div>
  );
}

export default ListPage;
