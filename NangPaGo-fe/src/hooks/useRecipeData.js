import { useState, useEffect } from 'react';
import {
  getLikeCount,
  fetchLikeStatus,
  fetchFavoriteStatus,
  toggleLike,
  toggleFavorite,
} from '../api/recipe';

const useRecipeData = (recipeId, isLoggedIn) => {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [showLoginModal, setShowLoginModal] = useState(false);

  useEffect(() => {
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
        console.error('데이터 초기화 중 오류:', error);
      }
    };

    initializeData();
  }, [recipeId, isLoggedIn]);

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      const { liked } = await toggleLike(recipeId);
      setIsHeartActive(liked);
      setLikeCount((prev) => (liked ? prev + 1 : prev - 1));
    } catch (error) {
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
