import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getMyPageInfo,
  getLikes,
  getFavorites,
  getComments,
} from '../../api/myPage.js';

import Header from '../../components/layout/header/Header.jsx';
import Footer from '../../components/common/Footer';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ItemList from '../../components/profile/ItemList';
import useMyPageInfo from '../../hooks/useMyPageInfo';
import useTabData from '../../hooks/useTabData';

function Profile() {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('likes');
  const { myPageInfo, totalCounts } = useMyPageInfo();
  const { items, isLoading, hasMore, fetchTabData } = useTabData(activeTab);

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
            onTabChange={(newTab) => {
              if (activeTab !== newTab) {
                setActiveTab(newTab);
              }
            }}
          />
          <div className="py-4">
            <ItemList
              items={items}
              activeTab={activeTab}
              hasMore={hasMore}
              onLoadMore={handleLoadMore}
              onItemClick={handleItemClick}
              isLoading={isLoading} // 로딩 상태 전달
            />
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );

  function handleLoadMore() {
    if (isLoading || !hasMore) return;
    fetchTabData({ page: Math.ceil(items.length / 7) + 1 });
  }

  function handleItemClick(id) {
    if (activeTab !== 'comments') {
      navigate(`/recipe/${id}`);
    }
  }
}

export default Profile;
