import React, { useState } from 'react';
import RecipeCard from './RecipeCard';

const RecipeListContent = ({ activeTab, images, searchTerm }) => {
  // 검색어로 이미지 필터링
  const filteredImages = images.filter(image => 
    image.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="grid grid-cols-1 gap-6">
      {activeTab === 'recommended' ? (
        // 필터링된 이미지 목록 표시
        filteredImages.map((image) => (
          <RecipeCard key={image.id} image={image} />
        ))
      ) : (
        // 즐겨찾기 목록 (현재는 빈 상태)
        <div className="text-center py-8 text-gray-500">
          즐겨찾기한 레시피가 없습니다.
        </div>
      )}
    </div>
  );
};

export default RecipeListContent; 