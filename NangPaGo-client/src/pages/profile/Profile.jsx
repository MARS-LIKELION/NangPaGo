import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import ProfileHeader from '../../components/profile/ProfileHeader';
import ProfileTabs from '../../components/profile/ProfileTabs';
import ItemList from '../../components/profile/ItemList';
import useMyPageInfo from '../../hooks/useMyPageInfo';
import useTabData from '../../hooks/useTabData';

function Profile() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('likes');
  const { myPageInfo, totalCounts } = useMyPageInfo();
  const { items, isLoading, hasMore, fetchTabData, currentPage } =
    useTabData(activeTab);

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
              isLoading={isLoading}
            />
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );

  function handleLoadMore() {
    if (isLoading || !hasMore) return;
    fetchTabData({ page: currentPage });
  }

  function handleItemClick(id) {
    if (activeTab !== 'comments') {
      navigate(`/recipe/${id}`);
    }
  }
}

export default Profile;
