import { BiSearch, BiX } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';

function SearchBar({ searchPath, searchTerm = '', onClear }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(searchPath, {
      state: { searchTerm },
    });
  };

  const clearSearchTerm = (e) => {
    e.stopPropagation();
    if (searchTerm) {
      onClear();
      navigate('/', {
        state: { searchTerm: '' },
        replace: true
      });
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] px-4 py-2 rounded-lg">
      <div
        className="relative flex items-center cursor-pointer border border-[var(--primary-color)] rounded-lg bg-white"
        onClick={handleClick}
      >
        <div className="w-full px-4 py-2 text-gray-500">
          {searchTerm || '레시피 검색...'}
        </div>
        {searchTerm ? (
          <BiX
            className="absolute right-3 text-[var(--secondary-color)] text-3xl"
            onClick={clearSearchTerm}
          />
        ) : (
          <BiSearch className="absolute right-3 text-[var(--secondary-color)] text-2xl" />
        )}
      </div>
    </div>
  );
}

export default SearchBar;
