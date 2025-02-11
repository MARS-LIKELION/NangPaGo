import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ItemList from '../../components/profile/ItemList';
import useMyPageInfo from '../../hooks/useMyPageInfo';
import useTabData from '../../hooks/useTabData';

function Profile() {
  const navigate = useNavigate();
  const location = useLocation();

  // URL에서 현재 탭을 가져오거나 기본값 설정
  const [activeTab, setActiveTab] = useState(() => {
    const params = new URLSearchParams(location.search);
    return params.get('tab') || 'likes';
  });

  // 탭 변경 시 URL 업데이트
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('tab') !== activeTab) {
      params.set('tab', activeTab);
      navigate(`?${params.toString()}`, { replace: true });
    }
  }, [activeTab, navigate, location.search]);

  const { myPageInfo, totalCounts } = useMyPageInfo();
  const { items, isLoading, hasMore, fetchTabData, currentPage } =
    useTabData(activeTab);

  function handleLoadMore() {
    if (isLoading || !hasMore) return;
    fetchTabData({ page: currentPage });
  }

  function handleItemClick(id) {
    if (activeTab !== 'comments') {
      navigate(`/recipe/${id}`);
    }
  }

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen flex flex-col justify-between max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <div>
        <Header />
        <div className="flex-1 px-4 bg-white">
          <ProfileHeader
            nickName={myPageInfo.nickName}
            providerName={myPageInfo.providerName}
          />
          <ProfileTabs
            activeTab={activeTab}
            totalCounts={totalCounts}
            onTabChange={(newTab) => setActiveTab(newTab)}
          />
          <div className="py-4">
            <ItemList
              items={items}
              activeTab={activeTab}
              hasMore={hasMore}
              onLoadMore={handleLoadMore}
              onItemClick={handleItemClick}
              isLoading={isLoading}
            />
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default Profile;
