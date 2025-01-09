import { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getMyPageInfo,
  getLikes,
  getFavorites,
  getComments,
} from '../../api/myPage.js';

import Header from '../../components/common/Header';
import Footer from '../../components/common/Footer';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ItemList from '../../components/profile/ItemList';

function Profile() {
  const [myPageInfo, setMyPageInfo] = useState({});
  const [activeTab, setActiveTab] = useState('likes');
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [totalCounts, setTotalCounts] = useState({
    likes: 0,
    favorites: 0,
    comments: 0,
  });
  const isFetchingRef = useRef(false);
  const navigate = useNavigate();
  const debounceTimeoutRef = useRef(null);

  useEffect(() => {
    getMyPageInfo()
      .then((info) => {
        setMyPageInfo(info);
        setTotalCounts({
          likes: info.likeCount || 0,
          favorites: info.favoriteCount || 0,
          comments: info.commentCount || 0,
        });
      })
      .catch(console.error);
  }, []);

  const fetchTabData = async (currentPage) => {
    if (isFetchingRef.current || !hasMore) return;

    isFetchingRef.current = true;

    try {
      let data = { content: [], last: true };
      switch (activeTab) {
        case 'likes':
          data = await getLikes(currentPage, 7);
          break;
        case 'favorites':
          data = await getFavorites(currentPage, 7);
          break;
        case 'comments':
          data = await getComments(currentPage, 7);
          break;
        default:
          data = { content: [], last: true };
      }

      setItems((prev) => [
        ...prev,
        ...data.content.filter(
          (item) => !prev.some((existingItem) => existingItem.id === item.id),
        ),
      ]);

      setHasMore(!data.last);
    } catch (error) {
      console.error('Failed to fetch tab data:', error);
    } finally {
      isFetchingRef.current = false;
    }
  };

  // 페이지 변경 감지하여 데이터 가져오기
  useEffect(() => {
    fetchTabData(page);
  }, [page]);

  useEffect(() => {
    setItems([]);
    setPage(1);
    setHasMore(true);
    isFetchingRef.current = false;
    
    if (debounceTimeoutRef.current) {
      clearTimeout(debounceTimeoutRef.current);
    }

    // 탭 변경 시 초기 데이터 로드
    fetchTabData(1);
  }, [activeTab]);

  const handleItemClick = (id) => {
    if (activeTab !== 'comments') {
      navigate(`/recipe/${id}`);
    }
  };

  const handleLoadMore = useCallback(() => {
    if (debounceTimeoutRef.current) {
      clearTimeout(debounceTimeoutRef.current);
    }

    debounceTimeoutRef.current = setTimeout(() => {
      // 현재 페이지가 1이고 새로운 요청이 시작되지 않았을 때만 실행
      if (page === 1) {
        isFetchingRef.current = false;
        fetchTabData(1);
      } else if (!isFetchingRef.current && hasMore) {
        setPage((prev) => prev + 1);
      }
    }, 300);
  }, [page, hasMore]);

  // cleanup effect 추가
  useEffect(() => {
    return () => {
      if (debounceTimeoutRef.current) {
        clearTimeout(debounceTimeoutRef.current);
      }
    };
  }, []);

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <div>
        <Header />
        <div className="flex-1 px-6 bg-white">
          <ProfileHeader
            nickName={myPageInfo.nickName}
            providerName={myPageInfo.providerName}
          />
          <ProfileTabs
            activeTab={activeTab}
            totalCounts={totalCounts}
            onTabChange={setActiveTab}
          />
          <div className="py-4">
            <ItemList
              items={items}
              activeTab={activeTab}
              hasMore={hasMore}
              onLoadMore={handleLoadMore}
              onItemClick={handleItemClick}
            />
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default Profile;
