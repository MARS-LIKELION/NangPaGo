import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import CommunityListContent from '../../components/community/CommunityListContent';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import TopButton from '../../components/button/TopButton';
import CreateButton from '../../components/button/CreateButton';
import { PAGE_STYLES, BUTTON_STYLES } from '../../common/styles/ListPage';

function CommunityList() {
  const navigate = useNavigate();

  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsTopButtonVisible(window.scrollY > 100);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleCreateClick = () => {
    navigate('/community/new', { state: { from: window.location.pathname } });
  };

  return (
    <div className={PAGE_STYLES.wrapper}>
      <Header />
      <main className={PAGE_STYLES.body}>
        <div className={PAGE_STYLES.header}>커뮤니티</div>
        <CommunityListContent />
      </main>
      <aside className={BUTTON_STYLES.wrapper}>
        <div className={BUTTON_STYLES.body}>
          <CreateButton
            onClick={handleCreateClick}
            isTopButtonVisible={isTopButtonVisible}
          />
          {isTopButtonVisible && <TopButton />}
        </div>
      </aside>
      <Footer />
    </div>
  );
}

export default CommunityList;
