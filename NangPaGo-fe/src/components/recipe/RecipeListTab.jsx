import React from 'react';

const RecipeListTab = ({ activeTab, setActiveTab }) => {
  return (
    <div className="flex border-b border-gray-200">
      <button
        className={`flex-1 py-3 text-center font-medium border-b-2 ${
          activeTab === 'recommended'
            ? 'border-blue-500 text-blue-500'
            : 'border-transparent text-gray-500 hover:text-gray-700'
        }`}
        onClick={() => setActiveTab('recommended')}
      >
        추천 레시피
      </button>
      
      <button
        className={`flex-1 py-3 text-center font-medium border-b-2 ${
          activeTab === 'favorites'
            ? 'border-blue-500 text-blue-500'
            : 'border-transparent text-gray-500 hover:text-gray-700'
        }`}
        onClick={() => setActiveTab('favorites')}
      >
        즐겨찾기
      </button>
    </div>
  );
};

export default RecipeListTab;