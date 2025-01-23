import { useState, useEffect } from 'react';
import { BiSearch, BiArrowBack, BiX } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';
import { addIngredient, searchIngredients } from '../../api/refrigerator';
import {
  parseHighlightedName,
  stripHtmlTags,
} from '../../components/util/stringUtil';
import SearchResult from '../../components/search/SearchResult';
import NoResult from '../../components/search/NoResult';
import EmptyState from '../../components/search/EmptyState';
import useDebounce from '../../hooks/useDebounce';

function RefrigeratorSearch() {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState([]);
  const [existingIngredientMessage, setExistingIngredientMessage] =
    useState('');
  const debouncedKeyword = useDebounce(keyword, 500);

  const handleChange = (e) => {
    const newKeyword = e.target.value;
    setKeyword(newKeyword);
    setExistingIngredientMessage('');
  };

  useEffect(() => {
    const fetchSearchResults = async () => {
      if (!debouncedKeyword.trim()) {
        setResults([]);
        return;
      }

      try {
        const data = await searchIngredients(debouncedKeyword);
        setResults(data);
      } catch (error) {
        console.error('검색 요청 실패:', error);
        setResults([]);
      }
    };

    fetchSearchResults();
  }, [debouncedKeyword]);

  const handleResultClick = async (highlightedName) => {
    const rawName = stripHtmlTags(highlightedName);
    try {
      await addIngredient(rawName);
      navigate('/refrigerator');
    } catch (error) {
      setExistingIngredientMessage(`${rawName} 이미 추가되어 있습니다.`);
      setKeyword('');
    }
  };

  const clearKeyword = (e) => {
    e.stopPropagation();
    setKeyword('');
    setExistingIngredientMessage('');
  };

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <div className="sticky top-0 bg-white px-4 py-2 flex items-center gap-2 border-b">
        <button onClick={() => navigate(-1)} className="p-2 rounded-full bg-white">
          <BiArrowBack className="text-2xl text-secondary" />
        </button>

        <div className="relative flex-1">
          <input
            type="text"
            placeholder="재료 검색"
            value={keyword}
            onChange={handleChange}
            autoFocus
            className="w-full px-4 py-2 border border-primary rounded-md
                       focus:outline-none focus:ring-2 focus:ring-primary
                       focus:border-transparent placeholder-text-400"
          />
          {keyword ? (
            <BiX
              className="absolute right-3 top-1/2
                        transform -translate-y-1/2
                        text-secondary
                        cursor-pointer text-3xl"
              onClick={clearKeyword}
            />
          ) : (
            <BiSearch
              className="absolute right-3 top-1/2 transform
                         -translate-y-1/2 cursor-pointer
                         text-secondary text-2xl"
            />
          )}
        </div>
      </div>

      <div className="px-4 py-2">
        {existingIngredientMessage ? (
          <div className="text-primary text-center mb-2">
            {existingIngredientMessage}
          </div>
        ) : keyword ? (
          results.length > 0 ? (
            <SearchResult
              results={results}
              parseHighlightedName={parseHighlightedName}
              onResultClick={handleResultClick}
            />
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

export default RefrigeratorSearch;
