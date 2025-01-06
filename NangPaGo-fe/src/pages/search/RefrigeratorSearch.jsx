import { useState } from 'react';
import { BiSearch, BiArrowBack } from 'react-icons/bi';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import parse from 'html-react-parser';
import { addIngredient } from '../../api/refrigerator';

function RefrigeratorSearch() {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState([]);
  const [existingIngredientMessage, setExistingIngredientMessage] =
    useState('');

  const handleChange = async (e) => {
    const newKeyword = e.target.value;
    setKeyword(newKeyword);
    setExistingIngredientMessage(''); // Clear the message when keyword changes

    if (!newKeyword.trim()) {
      setResults([]);
      return;
    }

    try {
      const response = await axios.get('/api/ingredient/search', {
        params: { keyword: newKeyword },
      });
      const { data } = response.data;
      setResults(data);
    } catch (error) {
      console.error('검색 요청 실패:', error);
      setResults([]);
    }
  };

  const parseHighlightedName = (htmlString) => {
    return parse(htmlString, {
      replace: (domNode) => {
        if (domNode.name === 'em') {
          return (
            <strong style={{ color: 'var(--secondary-color)' }}>
              {domNode.children[0]?.data}
            </strong>
          );
        }
      },
    });
  };

  const stripHtmlTags = (htmlString) => {
    return htmlString.replace(/<[^>]+>/g, '');
  };

  const handleResultClick = async (highlightedName) => {
    const rawName = stripHtmlTags(highlightedName);
    try {
      const addedIngredient = await addIngredient(rawName);
      navigate('/refrigerator');
    } catch (error) {
      setExistingIngredientMessage(`${rawName} 이미 추가되어 있습니다.`);
      setKeyword('');
    }
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

        <div className="relative flex-1">
          <input
            type="text"
            placeholder="재료 검색"
            value={keyword}
            onChange={handleChange}
            autoFocus
            className="w-full px-4 py-2 border border-[var(--primary-color)] rounded-lg
                       focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)]
                       focus:border-transparent placeholder-gray-500"
          />
          <BiSearch className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[var(--secondary-color)] text-xl" />
        </div>
      </div>

      <div className="px-4 py-2">
        {existingIngredientMessage ? (
          <div className="text-[var(--secondary-color)] text-center mb-2">
            {existingIngredientMessage}
          </div>
        ) : keyword ? (
          results.length > 0 ? (
            <SearchResults
              results={results}
              parseHighlightedName={parseHighlightedName}
              onResultClick={handleResultClick}
            />
          ) : (
            <NoResults searchTerm={keyword} />
          )
        ) : (
          <EmptyState />
        )}
      </div>
    </div>
  );
}

function SearchResults({ results, parseHighlightedName, onResultClick }) {
  return (
    <div>
      <ul>
        {results.map((item) => (
          <li
            key={item.ingredientId}
            onClick={() => onResultClick(item.highlightedName)}
            className="p-2 mb-2 cursor-pointer"
          >
            {parseHighlightedName(item.highlightedName)}
          </li>
        ))}
      </ul>
    </div>
  );
}

function NoResults({ searchTerm }) {
  return (
    <div className="text-center text-gray-500">
      <p>{searchTerm} 검색 결과가 없습니다.</p>
    </div>
  );
}

function EmptyState() {
  return (
    <div className="text-gray-500 text-center">
      <p>검색어를 입력해주세요.</p>
    </div>
  );
}

export default RefrigeratorSearch;
