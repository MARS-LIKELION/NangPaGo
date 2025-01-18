import { BiSearch, BiX } from 'react-icons/bi';

function SearchInput({ value, onChange, onClear, onSubmit }) {
  return (
      <form onSubmit={onSubmit} className="relative flex-1">
        <input
            type="text"
            placeholder="레시피 검색..."
            value={value}
            onChange={onChange}
            autoFocus
            className="w-full px-4 py-2 border border-primary rounded-md
                   focus:outline-none focus:ring-2 focus:ring-primary
                   focus:border-transparent placeholder-text-400"
            aria-label="검색어 입력"
        />
        {value ? (
            <button
                type="button"
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-secondary bg-white text-3xl"
                onClick={onClear}
                aria-label="검색어 삭제"
            >
              <BiX />
            </button>
        ) : (
            <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-secondary text-2xl" />
        )}
      </form>
  );
}

export default SearchInput;
