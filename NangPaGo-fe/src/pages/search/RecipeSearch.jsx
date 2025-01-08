import { useState, useEffect } from 'react';
import { BiSearch, BiArrowBack, BiX } from 'react-icons/bi';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import NoResult from '../../components/search/NoResult';
import EmptyState from '../../components/search/EmptyState';
import { parseHighlightedName } from '../../components/util/stringUtil';
import useDebounce from '../../hooks/useDebounce';

function RecipeSearch() {
  const navigate = useNavigate();
  const location = useLocation();
  const [keyword, setKeyword] = useState(location.state?.searchTerm || '');
  const [results, setResults] = useState([]);
  const debouncedKeyword = useDebounce(keyword, 500);

  const handleChange = (e) => {
    setKeyword(e.target.value);
  };

  const clearKeyword = (e) => {
    e.stopPropagation();
    setKeyword('');
  };

  useEffect(() => {
    const searchRecipes = async () => {
      if (!debouncedKeyword.trim()) {
        setResults([]);
        return;
      }

      try {
        const response = await axios.get('/api/recipe/search', {
          params: {
            pageNo: 1,
            pageSize: 10,
            keyword: debouncedKeyword,
            searchType: 'NAME',
          },
        });
        setResults(response.data.data.content);
      } catch (error) {
        console.error('레시피 검색 요청 실패:', error);
        setResults([]);
      }
    };

    searchRecipes();
  }, [debouncedKeyword]);

  const handleResultClick = (recipe) => {
    setKeyword(recipe.name);
    navigate('/', {
      state: { searchTerm: recipe.name },
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    navigate('/', {
      state: { searchTerm: keyword },
    });
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen">
      <div className="sticky top-0 bg-white px-4 py-2 flex items-center gap-2 border-b">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-full hover:bg-gray-100 transition"
        >
          <BiArrowBack className="text-2xl text-[var(--secondary-color)]" />
        </button>

        <form onSubmit={handleSubmit} className="relative flex-1">
          <input
            type="text"
            placeholder="레시피 검색..."
            value={keyword}
            onChange={handleChange}
            autoFocus
            className="w-full px-4 py-2 border border-[var(--primary-color)] rounded-lg 
                     focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] 
                     focus:border-transparent placeholder-gray-500"
          />
          {keyword ? (
            <BiX
              className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[var(--secondary-color)] text-3xl cursor-pointer"
              onClick={clearKeyword}
            />
          ) : (
            <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[var(--secondary-color)] text-2xl" />
          )}
        </form>
      </div>

      <div className="px-4 py-2">
        {keyword ? (
          results.length > 0 ? (
            <div className="space-y-2">
              {results.map((recipe) => (
                <div
                  key={recipe.id}
                  onClick={() => handleResultClick(recipe)}
                  className="p-3 rounded-lg cursor-pointer hover:bg-gray-50"
                >
                  <span className="text-black">
                    {parseHighlightedName(recipe.highlightedName)}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <NoResult searchTerm={keyword} />
          )
        ) : (
          <EmptyState />
        )}
      </div>
    </div>
  );
}

export default RecipeSearch;
