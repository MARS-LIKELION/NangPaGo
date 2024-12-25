import { useState } from 'react';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';
import Header from '../../components/common/Header.jsx';

function RecipeList() {
  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState('');

  const images = [
    { id: 1, url: 'https://picsum.photos/400/300?random=1', title: 'Image 1' },
    { id: 2, url: 'https://picsum.photos/400/300?random=2', title: 'Image 2' },
    { id: 3, url: 'https://picsum.photos/400/300?random=3', title: 'Image 3' },
    { id: 4, url: 'https://picsum.photos/400/300?random=4', title: 'Image 4' },
    { id: 5, url: 'https://picsum.photos/400/300?random=5', title: 'Image 5' },
    { id: 6, url: 'https://picsum.photos/400/300?random=6', title: 'Image 6' },
    { id: 7, url: 'https://picsum.photos/400/300?random=7', title: 'Image 7' },
    { id: 8, url: 'https://picsum.photos/400/300?random=8', title: 'Image 8' },
    { id: 9, url: 'https://picsum.photos/400/300?random=9', title: 'Image 9' },
    {
      id: 10,
      url: 'https://picsum.photos/400/300?random=10',
      title: 'Image 10',
    },
    {
      id: 11,
      url: 'https://picsum.photos/400/300?random=11',
      title: 'Image 11',
    },
    {
      id: 12,
      url: 'https://picsum.photos/400/300?random=12',
      title: 'Image 12',
    },
    {
      id: 13,
      url: 'https://picsum.photos/400/300?random=13',
      title: 'Image 13',
    },
    {
      id: 14,
      url: 'https://picsum.photos/400/300?random=14',
      title: 'Image 14',
    },
    {
      id: 15,
      url: 'https://picsum.photos/400/300?random=15',
      title: 'Image 15',
    },
    {
      id: 16,
      url: 'https://picsum.photos/400/300?random=16',
      title: 'Image 16',
    },
    {
      id: 17,
      url: 'https://picsum.photos/400/300?random=17',
      title: 'Image 17',
    },
    {
      id: 18,
      url: 'https://picsum.photos/400/300?random=18',
      title: 'Image 18',
    },
    {
      id: 19,
      url: 'https://picsum.photos/400/300?random=19',
      title: 'Image 19',
    },
    {
      id: 20,
      url: 'https://picsum.photos/400/300?random=20',
      title: 'Image 20',
    },
  ];

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
      {/* 헤더 */}
      <Header />

      {/* 콘텐츠 컨테이너 */}
      <div className="px-4 py-4 space-y-4">
        {/* 탭 메뉴 */}
        <RecipeListTab activeTab={activeTab} setActiveTab={setActiveTab} />

        {/* 서치바 */}
        <div className="flex justify-center">
          <SearchBar
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            className="w-[200px]"
          />
        </div>

        {/* 레시피 콘텐츠 */}
        <RecipeListContent
          activeTab={activeTab}
          images={images}
          searchTerm={searchTerm}
        />
      </div>
    </div>
  );
}

export default RecipeList;
