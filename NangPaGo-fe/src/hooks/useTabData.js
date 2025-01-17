import { useState, useEffect, useRef } from 'react';
import { getLikes, getFavorites, getComments } from '../api/myPage';

function useTabData(activeTab) {
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const abortControllers = useRef({});
  const currentTab = useRef(activeTab);
  const pendingRequest = useRef(false); // 중복 요청 방지 플래그

  const fetchTabDataByType = {
    likes: getLikes,
    favorites: getFavorites,
    comments: getComments,
  };

  async function fetchTabData({ page = 1, reset = false } = {}) {
    if (pendingRequest.current || (!hasMore && !reset)) return;

    setIsLoading(true);
    pendingRequest.current = true; // 요청 시작

    // 이전 요청 중단
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
        return; // 탭 변경된 경우 결과 무시
      }

      setItems((prev) => {
        if (reset) {
          return data.content;
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
      pendingRequest.current = false; // 요청 종료 또는 중단
    }
  }

  useEffect(() => {
    currentTab.current = activeTab;

    // 상태 초기화 및 이전 요청 중단
    setItems([]);
    setHasMore(true);
    setIsLoading(false);

    if (abortControllers.current[activeTab]) {
      abortControllers.current[activeTab].abort();
      pendingRequest.current = false; // 이전 요청 중단 후 초기화
    }

    // 새로운 API 요청 시작
    fetchTabData({ page: 1, reset: true });

    return () => {
      if (abortControllers.current[activeTab]) {
        abortControllers.current[activeTab].abort();
        pendingRequest.current = false; // 정리 단계에서 초기화
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
