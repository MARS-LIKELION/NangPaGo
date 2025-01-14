import { useState, useEffect, useRef } from 'react';
import { getLikes, getFavorites, getComments } from '../api/myPage';

function useTabData(activeTab) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const abortControllers = useRef({});
  const currentTab = useRef(activeTab);

  const fetchTabDataByType = {
    likes: getLikes,
    favorites: getFavorites,
    comments: getComments,
  };

  async function fetchTabData({ page = 1, reset = false } = {}) {
    if (isLoading || (!hasMore && !reset)) return;

    setIsLoading(true);

    if (abortControllers.current[activeTab]) {
      abortControllers.current[activeTab].abort();
    }
    const controller = new AbortController();
    abortControllers.current[activeTab] = controller;

    try {
      const fetchFunction = fetchTabDataByType[activeTab];
      const data = await fetchFunction(page, 7, {
        signal: controller.signal,
      });

      if (currentTab.current !== activeTab) {
        return;
      }

      setItems((prev) => {
        if (reset) {
          return data.content; // 초기화 시 새로운 데이터만 추가
        }
        return [
          ...prev,
          ...data.content.filter(
            (item) => !prev.some((existingItem) => existingItem.id === item.id),
          ),
        ];
      });

      setHasMore(!data.last);
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
    }
  }

  useEffect(() => {
    currentTab.current = activeTab;

    setItems([]);
    setHasMore(true);
    setIsLoading(false);

    fetchTabData({ page: 1, reset: true });

    return () => {
      if (abortControllers.current[activeTab]) {
        abortControllers.current[activeTab].abort();
      }
    };
  }, [activeTab]);

  return {
    items,
    isLoading,
    hasMore,
    fetchTabData,
  };
}

export default useTabData;
