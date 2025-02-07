import { useNavigate } from 'react-router-dom';
import { useEffect, useRef, useState } from 'react';
import { fetchPosts } from '../../api/community';
import CommunityCard from '../../components/community/CommunityCard';
import { PAGE_STYLES } from '../../common/styles/ListPage';
import { PAGE_INDEX, PAGE_SIZE } from '../../common/constants/pagination';

function CommunityListContent() {
  const [communityList, setCommunityList] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const navigate = useNavigate();

  const observerRef = useRef(null);
  const observerInstance = useRef(null);
  const isFetching = useRef(false);
  const pageSize = PAGE_SIZE.list;

  const loadCommunityList = async (page) => {
    if (isFetching.current || !hasMore) return;

    isFetching.current = true;

    try {
      const response = await fetchPosts(page, pageSize);
      const { content, last } = response.data;

      setCommunityList((prev) =>
        page === 0 ? content : [...prev, ...content],
      );
      setCurrentPage(page);
      setHasMore(!last);
    } catch (error) {
      console.error('커뮤니티 목록을 가져오는 중 오류 발생:', error);
      //       console.error(`Error loading ${type} recipeList:`, error);
    } finally {
      isFetching.current = false;
    }
  };

  useEffect(() => {
    loadCommunityList(0);
  }, []);

  useEffect(() => {
    if (!observerRef.current) return;

    observerInstance.current = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        loadCommunityList(currentPage + PAGE_INDEX.one);
      }
    });

    observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [currentPage, hasMore]);

  return (
    <ul className={PAGE_STYLES.list}>
      {communityList.map((community) => (
        <CommunityCard
          key={community.id}
          item={community}
          onClick={() => navigate(`/community/${community.id}`)}
        />
      ))}
      <div ref={observerRef}></div>
    </ul>
  );
}

export default CommunityListContent;
