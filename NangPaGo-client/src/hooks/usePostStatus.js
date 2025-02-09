import { useEffect, useState, useCallback } from 'react';
import {
  fetchFavoriteStatus,
  fetchLikeStatus,
  getLikeCount,
  toggleFavorite,
  toggleLike,
} from '../api/postStatus';
import { useSelector } from 'react-redux';

const usePostStatus = (post, isLoggedIn) => {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [likeCount, setLikeCount] = useState(null);
  const [modalState, setModalState] = useState({
    type: null,
    data: null,
  });
  const userId = useSelector((state) => state.loginSlice.userId);
  const [isReconnecting, setIsReconnecting] = useState(false);

  const initializeData = useCallback(async () => {
    try {
      const count = await getLikeCount(post);
      setLikeCount(count);

      if (isLoggedIn) {
        const likeStatus = await fetchLikeStatus(post);
        setIsHeartActive(likeStatus);
        if (post.type === 'recipe') {
          const favoriteStatus = await fetchFavoriteStatus(post);
          setIsStarActive(favoriteStatus);
        }
      }
    } catch (error) {
      console.error('초기 데이터 가져오기 오류:', error);
    }
  }, [post, isLoggedIn]);

  const handleSseError = useCallback(() => {
    if (!isReconnecting) {
      setIsReconnecting(true);
      setTimeout(() => {
        setIsReconnecting(false);
      }, 3000);
    }
  }, [isReconnecting]);

  useEffect(() => {
    initializeData();
  }, [initializeData]);

  useEffect(() => {
    let eventSource = null;

    eventSource = new EventSource(
      `/api/${post.type}/${post.id}/like/notification/subscribe`,
    );

    let eventName = '';
    if (post.type === 'community') {
      eventName = 'COMMUNITY_LIKE_EVENT';
    } else if (post.type === 'recipe') {
      eventName = 'RECIPE_LIKE_EVENT';
    } else if (post.type === 'user-recipe') {
      eventName = 'USER_RECIPE_LIKE_EVENT';
    }

    eventSource.addEventListener(eventName, (event) => {
      const eventData = JSON.parse(event.data);
      if (eventData.userId === userId) {
        return;
      }

      const updatedLikeCount = eventData.likeCount;

      setLikeCount(updatedLikeCount);
    });

    eventSource.onerror = () => {
      eventSource.close();
      handleSseError();
    };

    return () => {
      if (eventSource) {
        eventSource.close();
      }
    };
  }, [post, handleSseError]);

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setModalState({
        type: 'login',
        data: '좋아요는 로그인 후 사용 가능합니다.',
      });
      return;
    }

    try {
      // 낙관적 업데이트: 즉시 UI 반영
      setIsHeartActive((prev) => !prev);
      setLikeCount((prev) => prev + (isHeartActive ? -1 : 1));

      // 서버에 토글 요청만 보내고 응답은 기다리지 않음
      await toggleLike(post);

      // SSE로 최종 상태 업데이트를 받을 것이므로
      // 추가적인 상태 업데이트는 하지 않음
    } catch (error) {
      // 에러 발생 시 원래 상태로 복구
      setIsHeartActive((prev) => !prev);
      setLikeCount((prev) => prev + (isHeartActive ? 1 : -1));
      console.error('좋아요 상태 변경 오류:', error);
    }
  };

  const toggleStar = async () => {
    if (!isLoggedIn) {
      setModalState({
        type: 'login',
        data: '즐겨찾기는 로그인 후 사용 가능합니다.',
      });
      return;
    }

    try {
      // 낙관적 업데이트: 즉시 UI 반영
      setIsStarActive((prev) => !prev);

      // 서버에 토글 요청만 보내고 응답은 무시
      await toggleFavorite(post);

      // 서버 요청이 성공하면 낙관적 업데이트를 유지
    } catch (error) {
      // 에러 발생 시 원래 상태로 복구
      setIsStarActive((prev) => !prev);
      console.error('즐겨찾기 상태 변경 오류:', error);
    }
  };

  return {
    isHeartActive,
    isStarActive: post.type === 'recipe' ? isStarActive : undefined,
    likeCount,
    toggleHeart,
    toggleStar: post.type === 'recipe' ? toggleStar : undefined,
    modalState,
    setModalState,
  };
};

export default usePostStatus;
