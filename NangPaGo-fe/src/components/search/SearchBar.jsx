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
    if (!searchTerm) return;

    onClear();
    navigate('/', {
      state: { searchTerm: '' },
      replace: true,
    });
  };

  const getIcon = () => {
    if (searchTerm) {
      return (
        <BiX
          className="absolute right-3 text-secondary text-3xl cursor-pointer"
          onClick={clearSearchTerm}
        />
      );
    }

    return (
      <BiSearch className="absolute right-3 text-secondary text-2xl cursor-pointer" />
    );
  };

  return (
    <div className="bg-white shadow-md mx-auto w-full px-4 py-2 rounded-md">
      <div
        className="relative flex items-center border border-primary rounded-md bg-white cursor-pointer"
        onClick={handleClick}
      >
        <div className="w-full px-4 py-2 text-text-400">
          {searchTerm || '레시피 검색...'}
        </div>
        {getIcon()}
      </div>
    </div>
  );
}

export default SearchBar;
