import { useEffect, useRef, useState } from 'react';
import { fetchPostList } from '../../api/post';
import ContentCard from '../../components/common/ContentCard';
import { PAGE_STYLES } from '../../common/styles/ListPage';
import { PAGE_INDEX, PAGE_SIZE } from '../../common/constants/pagination';

function CommunityListContent() {
  const [communityList, setCommunityList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);

  const observerRef = useRef(null);
  const observerInstance = useRef(null);
  const isFetching = useRef(false);
  const pageSize = PAGE_SIZE.list;

  const loadCommunityList = async (page) => {
    if (isFetching.current || !hasMore) {
      return;
    }

    isFetching.current = true;

    try {
      const response = await fetchPostList('community', page, pageSize);
      const { content, last } = response.data;

      setCommunityList((prev) =>
        page === 0 ? content : [...prev, ...content],
      );
      setCurrentPage(page);
      setHasMore(!last);
    } catch (error) {
      console.error('커뮤니티 목록을 가져오는 중 오류 발생:', error);
    } finally {
      isFetching.current = false;
    }
  };

  // 초기 데이터 로드
  useEffect(() => {
    loadCommunityList(1);
  }, []);

  // 스크롤 이벤트 핸들러
  useEffect(() => {
    const checkAndLoadMore = () => {
      if (!observerRef.current || isFetching.current || !hasMore) {
        return;
      }

      const container = document.documentElement;
      const scrollHeight = container.scrollHeight;
      const scrollTop = container.scrollTop;
      const clientHeight = container.clientHeight;

      // 스크롤이 없는 경우에도 다음 페이지 로드
      if (
        scrollHeight <= clientHeight ||
        scrollHeight - (scrollTop + clientHeight) < 100
      ) {
        loadCommunityList(currentPage + PAGE_INDEX.one);
      }
    };

    // 초기 화면에서 컨텐츠가 화면을 채우지 못하는 경우 추가 로드
    checkAndLoadMore();

    // Intersection Observer 설정
    if (!observerRef.current) {
      return;
    }

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          checkAndLoadMore();
        }
      },
      {
        threshold: 0.1,
        rootMargin: '100px', // 하단에서 100px 전에 미리 로드 시작
      },
    );

    observerInstance.current.observe(observerRef.current);

    // 스크롤 이벤트 리스너 추가
    window.addEventListener('scroll', checkAndLoadMore);

    return () => {
      if (observerInstance.current) {
        observerInstance.current.disconnect();
      }
      window.removeEventListener('scroll', checkAndLoadMore);
    };
  }, [currentPage, hasMore]);

  return (
    <ul className={PAGE_STYLES.list}>
      {communityList.map((community) => (
        <ContentCard type={'community'} data={community} />
      ))}
      {hasMore && (
        <div ref={observerRef} style={{ height: '20px', opacity: 0 }}></div>
      )}
    </ul>
  );
}

export default CommunityListContent;
