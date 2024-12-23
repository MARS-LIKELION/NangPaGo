import React from 'react';
import { BiSearch } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';

const SearchBar = () => {
  const navigate = useNavigate();

  return (
    <div className="px-4 py-2">
      <div 
        className="relative cursor-pointer"
        onClick={() => navigate('/search')}
      >
        <div className="w-full px-4 py-2 border border-gray-300 rounded-lg text-gray-400 bg-gray-50">
          레시피 검색...
        </div>
        <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl" />
      </div>
    </div>
  );
};

export default SearchBar;
