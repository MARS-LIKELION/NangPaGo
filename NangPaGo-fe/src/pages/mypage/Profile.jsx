import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getRefrigerator,
  getLikes,
  getComments,
  getFavorites,
  getMyPageInfo,
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
  const [page, setPage] = useState(0); // 페이지 상태
  const [hasMore, setHasMore] = useState(true);
  const [totalCounts, setTotalCounts] = useState({
    likes: 0,
    comments: 0,
    favorites: 0,
    refrigerator: 0,
  });
  const isFetchingRef = useRef(false);
  const navigate = useNavigate();

  // 사용자 프로필 정보 가져오기
  useEffect(() => {
    getMyPageInfo()
      .then((info) => {
        setMyPageInfo(info);
        setTotalCounts({
          likes: info.likeCount || 0,
          comments: info.commentCount || 0,
          favorites: info.favoriteCount || 0,
          refrigerator: info.refrigeratorCount || 0,
        });
      })
      .catch(console.error);
  }, []);

  // 활성 탭의 데이터를 가져오는 함수
  const fetchTabData = async (currentPage) => {
    if (isFetchingRef.current || !hasMore) return;

    isFetchingRef.current = true;

    try {
      let data = { content: [], last: true };
      switch (activeTab) {
        case 'refrigerator':
          data = await getRefrigerator(currentPage, 7);
          break;
        case 'comments':
          data = await getComments(currentPage, 7);
          break;
        case 'likes':
          data = await getLikes(currentPage, 7);
          break;
        case 'favorites':
          data = await getFavorites(currentPage, 7);
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

  // 탭 변경 시 상태 초기화
  useEffect(() => {
    setItems([]);
    setPage(0);
    setHasMore(true);
  }, [activeTab]);

  // 아이템 클릭 이벤트
  const handleItemClick = (id) => {
    if (activeTab !== 'comments') {
      navigate(`/recipe/${id}`);
    }
  };

  const handleLoadMore = () => {
    if (!isFetchingRef.current) {
      setPage((prev) => prev + 1);
    }
  };

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
