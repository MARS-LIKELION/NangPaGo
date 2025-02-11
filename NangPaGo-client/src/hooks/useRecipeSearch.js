import { useState, useEffect } from 'react';
import useDebounce from './useDebounce';
import { fetchSearchSuggestions, searchPostsByKeyword } from '../api/recipe';

export const useRecipeSearch = (initialKeyword = '', debounceDelay = 500) => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [suggestions, setSuggestions] = useState([]);
  const [results, setResults] = useState([]);
  const debouncedKeyword = useDebounce(keyword, debounceDelay);

  useEffect(() => {
    const fetchSuggestions = async () => {
      if (!debouncedKeyword.trim()) {
        setSuggestions([]);
        return;
      }
      const data = await fetchSearchSuggestions(debouncedKeyword);

      setSuggestions(data);
    };
    fetchSuggestions();
  }, [debouncedKeyword]);

  const fetchSearchResults = async (query) => {
    if (!query.trim()) return;
    const data = await searchPostsByKeyword(query);

    setResults(data);
  };

  return { keyword, setKeyword, suggestions, results, fetchSearchResults };
};
