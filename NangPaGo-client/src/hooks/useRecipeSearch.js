import { useState, useEffect } from 'react';
import useDebounce from './useDebounce';
import { searchPostsByKeyword } from '../api/recipe';

export const useRecipeSearch = (initialKeyword = '', debounceDelay = 500) => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [results, setResults] = useState([]);
  const debouncedKeyword = useDebounce(keyword, debounceDelay);

  useEffect(() => {
    const fetchResults = async () => {
      if (!debouncedKeyword.trim()) {
        setResults([]);
        return;
      }

      const data = await searchPostsByKeyword(debouncedKeyword);
      setResults(data);
    };

    fetchResults();
  }, [debouncedKeyword]);

  return { keyword, setKeyword, results };
};
