import { useEffect, useRef, useState } from 'react';
import { fetchComments } from '../api/comment';

function useCommentsData(post) {
  const [comments, setComments] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [error, setError] = useState(null);
  const abortControllers = useRef({});
  const pendingRequest = useRef(false);

  async function fetchCommentsData({ page = 1, reset = false } = {}) {
    if (pendingRequest.current || (!hasMore && !reset)) {
      return;
    }

    setIsLoading(true);
    setError(null);
    pendingRequest.current = true;

    if (abortControllers.current[post.id]) {
      abortControllers.current[post.id].abort();
    }
    const controller = new AbortController();
    abortControllers.current[post.id] = controller;

    try {
      const response = await fetchComments(post, page, 5, {
        signal: controller.signal,
      });

      if (!response) {
        throw new Error(
          `API 응답이 없습니다. (postId: ${post.id}, page: ${page})`,
        );
      }

      const { content, totalPages, totalItems } = response.data;
      setComments((prev) => (reset ? content : [...prev, ...content]));
      setTotalPages(totalPages);
      setTotalItems(totalItems);
      setHasMore(page < totalPages);
      setCurrentPage(page + 1);
    } catch (error) {
      if (error.name === 'AbortError') {
        setError(`요청이 취소되었습니다. (postId: ${post.id})`);
      } else {
        setError(
          `데이터 로딩 실패: ${error.message} (postId: ${post.id}, page: ${page})`,
        );
      }
    } finally {
      setIsLoading(false);
      pendingRequest.current = false;
    }
  }

  useEffect(() => {
    if (post) {
      fetchCommentsData({ page: 1, reset: true });
    }
    return () => {
      if (abortControllers.current[post.id]) {
        abortControllers.current[post.id].abort();
        pendingRequest.current = false;
      }
    };
  }, [post]);

  return {
    comments,
    isLoading,
    hasMore,
    fetchCommentsData,
    currentPage,
    totalPages,
    totalItems,
    error,
  };
}

export default useCommentsData;
