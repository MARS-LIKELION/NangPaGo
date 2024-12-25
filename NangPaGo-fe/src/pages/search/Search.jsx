import { useState } from 'react';
import { BiSearch, BiArrowBack } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';

function Search() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
      <div className="sticky top-0 bg-white px-4 py-2 flex items-center gap-2 border-b">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-full hover:bg-gray-100 transition"
        >
          <BiArrowBack className="text-2xl text-[var(--secondary-color)]" />
        </button>

        <div className="relative flex-1">
          <input
            type="text"
            placeholder="레시피 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            autoFocus
            className="w-full px-4 py-2 border border-[var(--primary-color)] rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent placeholder-[var(--secondary-color)]"
          />
          <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[var(--secondary-color)] text-xl" />
        </div>
      </div>

      <div className="px-4 py-6">
        {searchTerm ? (
          <SearchResults searchTerm={searchTerm} />
        ) : (
          <EmptyState />
        )}
      </div>
    </div>
  );
}

function SearchResults({ searchTerm }) {
  return (
    <div>
      <p className="text-[var(--secondary-color)] text-sm mb-4">
        "{searchTerm}" 검색 결과
      </p>
      <div className="text-center text-gray-500">검색 결과가 없습니다.</div>
    </div>
  );
}

function EmptyState() {
  return (
    <div className="text-gray-500 text-center">
      <p>검색어를 입력해주세요</p>
    </div>
  );
}

export default Search;
