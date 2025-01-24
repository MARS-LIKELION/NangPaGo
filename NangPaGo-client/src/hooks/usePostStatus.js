import { useEffect, useState } from 'react';
import {
  fetchFavoriteStatus,
  fetchLikeStatus,
  getLikeCount,
  toggleFavorite,
  toggleLike,
} from '../api/postStatus';

const usePostStatus = (post, isLoggedIn) => {
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [isReconnecting, setIsReconnecting] = useState(false);

  const initializeData = async () => {
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
  }, [post, isLoggedIn]);

  useEffect(() => {
    if (post.type === 'community') { // TODO: 향후 커뮤니티 좋아요(SSE)를 가져오는 것이 구현된다면, 지워야함.
      return;
    }
    const eventSource = new EventSource(
      `/api/${post.type}/${post.id}/like/notification/subscribe`
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
  }, [post]);

  const toggleHeart = async () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    try {
      setIsHeartActive(!isHeartActive);
      setLikeCount((prev) => (isHeartActive ? prev - 1 : prev + 1));
      await toggleLike(post);
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
      const { favorited } = await toggleFavorite(post);
      setIsStarActive(favorited);
    } catch (error) {
      console.error('즐겨찾기 상태 변경 오류:', error);
    }
  };

  return {
    isHeartActive,
    isStarActive: post.type === "recipe" ? isStarActive : undefined,
    likeCount,
    showLoginModal,
    toggleHeart,
    toggleStar: post.type === "recipe" ? toggleStar : undefined,
    setShowLoginModal,
  };
};

export default usePostStatus;
