function SearchResult({ results, parseHighlightedName, onResultClick }) {
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

export default SearchResult;
