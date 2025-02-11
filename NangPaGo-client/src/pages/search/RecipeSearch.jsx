import { useNavigate, useLocation } from 'react-router-dom';
import { useRecipeSearch } from '../../hooks/useRecipeSearch';
import SearchInput from '../../components/search/SearchInput';
import SearchResult from '../../components/search/SearchResult';
import { parseHighlightedName } from '../../components/util/stringUtil';
import { BiArrowBack } from 'react-icons/bi';

function RecipeSearch() {
  const navigate = useNavigate();
  const location = useLocation();
  const { keyword, setKeyword, suggestions, results, fetchSearchResults } = useRecipeSearch(
    location.state?.searchTerm || '',
  );

  function handleChange(e) {
    setKeyword(e.target.value);
  }

  function clearKeyword(e) {
    e.stopPropagation();
    setKeyword('');
  }

  function handleResultClick(recipe) {
    setKeyword(recipe.title);
    navigate('/', {
      state: { searchTerm: recipe.title },
    });
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (!keyword.trim()) return;
    fetchSearchResults(keyword); // 🔹 일반 검색 실행
    navigate('/', {
      state: { searchTerm: keyword },
    });
  }

  return (
    <div className="bg-white shadow-md mx-auto min-h-screen min-w-80 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <div className="sticky top-0 bg-white px-4 py-2 flex items-center gap-2 border-b">
        <button
          onClick={() => navigate(-1)}
          className="bg-white p-2 rounded-full transition"
          aria-label="뒤로가기"
        >
          <BiArrowBack className="text-text-400 text-2xl" />
        </button>
        <SearchInput
          value={keyword}
          onChange={handleChange}
          onClear={clearKeyword}
          onSubmit={handleSubmit}
        />
      </div>

      {/* 🔹 추천 검색 결과 표시 */}
      <div className="px-4 py-2">
        <SearchResult
          results={suggestions}
          parseHighlightedName={parseHighlightedName}
          onResultClick={handleResultClick}
        />
      </div>

      {/* 🔹 일반 검색 결과 표시 */}
      {results.length > 0 && (
        <div className="px-4 py-2">
          <h2 className="text-lg font-semibold mb-2">검색 결과</h2>
          <SearchResult
            results={results}
            parseHighlightedName={parseHighlightedName}
            onResultClick={handleResultClick}
          />
        </div>
      )}
    </div>
  );
}

export default RecipeSearch;
