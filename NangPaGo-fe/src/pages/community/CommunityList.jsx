import { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchCommunityList } from '../../api/community';
import { AiFillHeart } from 'react-icons/ai';
import { FaCommentAlt } from 'react-icons/fa';
import TopButton from '../../components/common/TopButton';
import CreateButton from '../../components/common/CreateButton';
import Header from '../../components/common/Header';
import Footer from '../../components/common/Footer';
import CommunityCard from '../../components/community/CommunityCard';

function CommunityList() {
  const [communityList, setCommunityList] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  const observerRef = useRef(null);
  const isFetchingRef = useRef(false);
  const observerInstance = useRef(null);
  const pageSize = 5;
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
      setIsInitialLoad(false);
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
    if (!observerRef.current || isInitialLoad) return;

    observerInstance.current = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        loadCommunityList(currentPage + 1);
      }
    });

    observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [hasMore, currentPage, isInitialLoad]);

  const handleCreateClick = () => {
    navigate('/community/new');
  };

  const handleCardClick = (id) => {
    navigate(`/community/${id}`);
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />

      <div className="flex-grow px-4 space-y-4">
        {
          <ul className="space-y-4">
            {communityList.map((community) => (
              <CommunityCard
                key={community.id}
                item={community}
                onClick={handleCardClick}
              />
            ))}
            <div ref={observerRef} className="h-4"></div>
          </ul>
        }
      </div>

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

      <Footer />
    </div>
  );
}

export default CommunityList;
