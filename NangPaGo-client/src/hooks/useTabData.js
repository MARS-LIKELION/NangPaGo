import { useEffect, useRef, useState } from 'react';
import { getComments, getFavorites, getLikes, getPosts } from '../api/myPage';

function useTabData(activeTab) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [error, setError] = useState(null);
  const abortControllers = useRef({});
  const pendingRequest = useRef(false);

  const fetchTabDataByType = {
    likes: getLikes,
    favorites: getFavorites,
    posts: getPosts,
    comments: getComments,
  };

  async function fetchTabData({ page = 1, reset = false } = {}) {
    if (pendingRequest.current || (!hasMore && !reset)) return;

    setIsLoading(true);
    setError(null);
    pendingRequest.current = true;

    if (abortControllers.current[activeTab]) {
      abortControllers.current[activeTab].abort();
    }
    const controller = new AbortController();
    abortControllers.current[activeTab] = controller;

    try {
      const fetchFunction = fetchTabDataByType[activeTab];

      const response = await fetchFunction(page, 12, {
        signal: controller.signal,
      });

      if (!response) {
        throw new Error(`API 응답이 없습니다. (탭: ${activeTab})`);
      }

      const { content, last } = response.data;

      setItems((prev) => (reset ? content : [...prev, ...content]));
      setHasMore(!last);
      setCurrentPage((prev) => prev + 1);
    } catch (error) {
      if (error.name === 'AbortError') {
        setError(`요청이 취소되었습니다. (탭: ${activeTab})`);
      } else {
        setError(
          `데이터 로딩 실패: ${error.message} (탭: ${activeTab}, 페이지: ${page})`,
        );
      }
    } finally {
      setIsLoading(false);
      pendingRequest.current = false;
    }
  }

  useEffect(() => {
    setItems([]);
    setHasMore(true);
    setIsLoading(false);
    setCurrentPage(1);
    setError(null);

    if (abortControllers.current[activeTab]) {
      abortControllers.current[activeTab].abort();
      pendingRequest.current = false;
    }

    fetchTabData({ page: 1, reset: true });

    return () => {
      if (abortControllers.current[activeTab]) {
        abortControllers.current[activeTab].abort();
        pendingRequest.current = false;
      }
    };
  }, [activeTab]);

  return {
    items,
    isLoading,
    hasMore,
    fetchTabData,
    currentPage,
    error,
  };
}

export default useTabData;
