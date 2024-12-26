import { BiSearch } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';

function SearchBar() {
  const navigate = useNavigate();

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] px-4 py-2 rounded-lg">
      <div
        className="relative flex items-center cursor-pointer border border-[var(--primary-color)] rounded-lg bg-white"
        onClick={() => navigate('/search')}
      >
        <div className="w-full px-4 py-2 text-gray-500">레시피 검색...</div>
        <BiSearch className="absolute right-3 text-[var(--secondary-color)] text-xl" />
      </div>
    </div>
  );
}

export default SearchBar;
