import { useEffect, useState } from 'react';
import {
  fetchFavoriteStatus,
  fetchLikeStatus,
  getLikeCount,
  toggleFavorite,
  toggleLike,
} from '../api/recipe';

const useRecipeData = (recipeId, isLoggedIn) => {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isReconnecting, setIsReconnecting] = useState(false);

  const initializeData = async () => {
    try {
      const count = await getLikeCount(recipeId);
      setLikeCount(count);

      if (isLoggedIn) {
        const [likeStatus, favoriteStatus] = await Promise.all([
          fetchLikeStatus(recipeId),
          fetchFavoriteStatus(recipeId),
        ]);
        setIsHeartActive(likeStatus);
        setIsStarActive(favoriteStatus);
      }
    } catch (error) {
      console.error('초기 데이터 가져오기 오류:', error);
    }
  };

  const handleSseError = () => {
    if (!isReconnecting) {
      setIsReconnecting(true);
      setTimeout(() => {
        setIsReconnecting(false);
      }, 3000);
    }
  };

  useEffect(() => {
    initializeData();
  }, [recipeId, isLoggedIn]);

  useEffect(() => {
    const eventSource = new EventSource(
      `/api/recipe/${recipeId}/like/notification/subscribe`,
    );

    eventSource.addEventListener('좋아요 이벤트 발생', (event) => {
      const updatedLikeCount = parseInt(event.data, 10);
      setLikeCount((prevCount) =>
        prevCount !== updatedLikeCount ? updatedLikeCount : prevCount,
      );
    });

    eventSource.onerror = () => {
      eventSource.close();
      handleSseError();
    };

    return () => {
      eventSource.close();
    };
  }, [recipeId]);

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      setIsHeartActive(!isHeartActive);
      setLikeCount((prev) => (isHeartActive ? prev - 1 : prev + 1));
      await toggleLike(recipeId);
    } catch (error) {
      setIsHeartActive(!isHeartActive);
      setLikeCount((prev) => (isHeartActive ? prev - 1 : prev + 1));
      console.error('좋아요 상태 변경 오류:', error);
    }
  };

  const toggleStar = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const { favorited } = await toggleFavorite(recipeId);
      setIsStarActive(favorited);
    } catch (error) {
      console.error('즐겨찾기 상태 변경 오류:', error);
    }
  };

  return {
    isHeartActive,
    isStarActive,
    likeCount,
    showLoginModal,
    toggleHeart,
    toggleStar,
    setShowLoginModal,
  };
};

export default useRecipeData;
