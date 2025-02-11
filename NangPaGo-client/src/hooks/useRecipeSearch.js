import { useState, useEffect } from 'react';
import useDebounce from './useDebounce';
import { fetchSearchSuggestions, searchPostsByKeyword } from '../api/recipe';

export const useRecipeSearch = (initialKeyword = '', debounceDelay = 500) => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [suggestions, setSuggestions] = useState([]); // 추천 검색 목록
  const [results, setResults] = useState([]); // 일반 검색 결과
  const debouncedKeyword = useDebounce(keyword, debounceDelay);

  // 🔹 추천 검색 호출 (search/keyword)
  useEffect(() => {
    const fetchSuggestions = async () => {
      if (!debouncedKeyword.trim()) {
        setSuggestions([]);
        return;
      }
      const data = await fetchSearchSuggestions(debouncedKeyword);
      
      console.log('🔍 추천 검색 목록:', data);

      setSuggestions(data);
    };
    fetchSuggestions();
  }, [debouncedKeyword]);

  // 🔹 일반 검색 실행 함수 (search)
  const fetchSearchResults = async (query) => {
    if (!query.trim()) return;
    const data = await searchPostsByKeyword(query);

    console.log('🔍 일반 검색 목록:', data);
    setResults(data);
  };

  return { keyword, setKeyword, suggestions, results, fetchSearchResults };
};
