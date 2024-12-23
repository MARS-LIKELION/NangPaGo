import React, { useState } from 'react';
import { BiSearch, BiArrowBack } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';

const SearchPage = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <div className="mx-auto w-[375px]">
      <div className="sticky top-0 bg-white px-4 py-2 flex items-center gap-2 border-b">
        <button
          onClick={() => navigate(-1)}
          className="p-1"
        >
          <BiArrowBack className="text-2xl" />
        </button>
        
        <div className="relative flex-1">
          <input
            type="text"
            placeholder="레시피 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            autoFocus
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent"
          />
          <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl" />
        </div>
      </div>

      {/* 검색 결과 영역 */}
      <div className="px-4 py-6">
        {searchTerm ? (
          <div>검색 결과가 여기에 표시됩니다</div>
        ) : (
          <div className="text-gray-500 text-center">
            검색어를 입력해주세요
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchPage;
