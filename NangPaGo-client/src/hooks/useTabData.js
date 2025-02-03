import { useEffect, useRef, useState } from 'react';
import { getComments, getFavorites, getLikes, getPosts } from '../api/myPage';

function useTabData(activeTab) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const abortControllers = useRef({});
  const currentTab = useRef(activeTab);
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
    pendingRequest.current = true;

    if (abortControllers.current[activeTab]) {
      abortControllers.current[activeTab].abort();
    }
    const controller = new AbortController();
    abortControllers.current[activeTab] = controller;

    try {
      const fetchFunction = fetchTabDataByType[activeTab];
      console.log(`[DEBUG] Fetching data - Tab: ${activeTab}, Page: ${page}`);

      const response = await fetchFunction(page, 12, {
        signal: controller.signal,
      });

      if (!response) {
        throw new Error(
          `[ERROR] API response is undefined for tab: ${activeTab}`,
        );
      }

      console.log(`[DEBUG] API Response:`, response);

      const { content, last } = response.data;

      console.log(
        `[DEBUG] Content Length: ${content.length}, Last: ${last}, Current Page: ${page}`,
      );

      setItems((prev) => (reset ? content : [...prev, ...content]));
      setHasMore(!last);

      setCurrentPage((prev) => prev + 1);
    } catch (error) {
      if (error.name === 'AbortError') {
        console.warn(`[REQUEST ABORTED] Tab: ${activeTab}`);
      } else {
        console.error(
          `[REQUEST FAILED] Tab: ${activeTab}, Page: ${page}`,
          error,
        );
      }
    } finally {
      setIsLoading(false);
      pendingRequest.current = false;
    }
  }

  useEffect(() => {
    currentTab.current = activeTab;
    setItems([]);
    setHasMore(true);
    setIsLoading(false);
    setCurrentPage(1);

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
  };
}

export default useTabData;
