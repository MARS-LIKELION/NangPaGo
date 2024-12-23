import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import RecipeListTab from '../../components/recipe/RecipeListTab';
import RecipeListContent from '../../components/recipe/RecipeListContent';
import SearchBar from '../../components/search/SearchBar';

const RecipeList = () => {
  const [activeTab, setActiveTab] = useState('recommended');
  const [searchTerm, setSearchTerm] = useState('');
  
  // 임시 이미지 데이터 배열
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
    { id: 10, url: 'https://picsum.photos/400/300?random=10', title: 'Image 10' },
    { id: 11, url: 'https://picsum.photos/400/300?random=11', title: 'Image 11' },
    { id: 12, url: 'https://picsum.photos/400/300?random=12', title: 'Image 12' },
    { id: 13, url: 'https://picsum.photos/400/300?random=13', title: 'Image 13' },
    { id: 14, url: 'https://picsum.photos/400/300?random=14', title: 'Image 14' },
    { id: 15, url: 'https://picsum.photos/400/300?random=15', title: 'Image 15' },
    { id: 16, url: 'https://picsum.photos/400/300?random=16', title: 'Image 16' },
    { id: 17, url: 'https://picsum.photos/400/300?random=17', title: 'Image 17' },
    { id: 18, url: 'https://picsum.photos/400/300?random=18', title: 'Image 18' },
    { id: 19, url: 'https://picsum.photos/400/300?random=19', title: 'Image 19' },
    { id: 20, url: 'https://picsum.photos/400/300?random=20', title: 'Image 20' },
  ];

  return (
    <div className="mx-auto w-[375px]">
      {/* 상단 고정 영역 */}
      <div className="sticky top-0 z-10 bg-white">
        <div className="px-4 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-3xl font-bold">냉파고</h1>
            <Link 
              to="/login" 
              className="bg-[var(--primary-color)] hover:bg-[color-mix(in_srgb,var(--primary-color),#000_10%)] text-white px-4 py-2 rounded-lg transition-colors duration-300"
            >
              로그인
            </Link>
          </div>
        </div>
        
        <div className="px-4 pb-1">
          <RecipeListTab activeTab={activeTab} setActiveTab={setActiveTab} />
        </div>

        <SearchBar 
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
        />
      </div>

      {/* 스크롤되는 컨텐츠 영역 */}
      <div className="px-4 py-6">
        <RecipeListContent 
          activeTab={activeTab} 
          images={images} 
          searchTerm={searchTerm}
        />
      </div>
    </div>
  );
};

export default RecipeList;
