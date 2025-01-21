import NoResult from './NoResult';

function SearchResult({ results, parseHighlightedName, onResultClick }) {
  if (!results.length) return <NoResult />;

  return (
    <div className="space-y-2">
      {results.map((recipe) => (
        <div
          key={recipe.id}
          onClick={() => onResultClick(recipe)}
          className="p-3 rounded-lg cursor-pointer hover:text-white transition duration-200"
        >
          <span className="text-black">
            {parseHighlightedName(recipe.highlightedName)}
          </span>
        </div>
      ))}
    </div>
  );
}

export default SearchResult;
