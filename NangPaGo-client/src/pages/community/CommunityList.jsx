import { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchCommunityList } from '../../api/community';
import TopButton from '../../components/common/TopButton';
import CreateButton from '../../components/common/CreateButton';
import Header from '../../components/layout/header/Header.jsx';
import Footer from '../../components/common/Footer';
import CommunityCard from '../../components/community/CommunityCard';

function CommunityList() {
  const [communityList, setCommunityList] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const observerRef = useRef(null);
  const isFetchingRef = useRef(false);
  const observerInstance = useRef(null);
  const pageSize = 12;
  const navigate = useNavigate();

  const loadCommunityList = async (page) => {
    if (isFetchingRef.current || !hasMore) return;

    isFetchingRef.current = true;

    try {
      const response = await fetchCommunityList(page, pageSize);
      const { content, last } = response.data;

      setCommunityList((prev) =>
        page === 0 ? content : [...prev, ...content],
      );
      setCurrentPage(page);
      setHasMore(!last);
    } catch (error) {
      console.error('커뮤니티 목록을 가져오는 중 오류 발생:', error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  useEffect(() => {
    loadCommunityList(0);
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      setIsTopButtonVisible(window.scrollY > 100);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    if (!observerRef.current) return;

    observerInstance.current = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        loadCommunityList(currentPage + 1);
      }
    });

    observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [currentPage, hasMore]);

  const handleCreateClick = () => {
    navigate('/community/new', { state: { from: window.location.pathname } });
  };

  const handleCardClick = (id) => {
    navigate(`/community/${id}`);
  };

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen flex flex-col max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header />

      <div className="flex-grow px-4 space-y-4">
        <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 md:gap-2 lg:gap-4 md:mb-2 lg:mb-4">
          {communityList.map((community) => (
            <CommunityCard
              key={community.id}
              item={community}
              onClick={handleCardClick}
            />
          ))}
          <div ref={observerRef}></div>
        </ul>
      </div>

      <aside className="fixed z-50 left-0 right-0 bottom-0 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg mx-auto">
        <div className="flex justify-end gap-4 px-4">
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
