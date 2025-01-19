import { useState, useEffect } from 'react';
import { getMyPageInfo } from '../api/myPage';

function useMyPageInfo() {
  const [myPageInfo, setMyPageInfo] = useState({});
  const [totalCounts, setTotalCounts] = useState({
    likes: 0,
    favorites: 0,
    comments: 0,
  });

  useEffect(() => {
    async function fetchMyPageInfo() {
      try {
        const info = await getMyPageInfo();
        setMyPageInfo(info);
        setTotalCounts({
          likes: info.likeCount || 0,
          favorites: info.favoriteCount || 0,
          comments: info.commentCount || 0,
        });
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    }

    fetchMyPageInfo();
  }, []);

  return { myPageInfo, totalCounts };
}

export default useMyPageInfo;
