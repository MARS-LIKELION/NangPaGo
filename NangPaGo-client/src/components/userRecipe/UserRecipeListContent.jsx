import { useNavigate } from 'react-router-dom';
import { useEffect, useState, useRef } from 'react';
import { fetchPostList } from '../../api/post';
import ContentCard from '../../components/common/ContentCard';
import { PAGE_STYLES } from '../../common/styles/ListPage';
import { PAGE_INDEX, PAGE_SIZE } from '../../common/constants/pagination';

function UserRecipeListContent() {
  const [recipeList, setRecipeList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const navigate = useNavigate();

  const observerRef = useRef(null);
  const observerInstance = useRef(null);
  const isFetching = useRef(false);
  const pageSize = PAGE_SIZE.list;

  const loadUserRecipeList = async (page) => {
    if (isFetching.current || !hasMore) return;
    isFetching.current = true;

    try {
      const response = await fetchPostList("user-recipe", page, pageSize);
      const { content, last } = response.data;

      setRecipeList((prev) =>
        page === 0 ? content : [...prev, ...content]
      );
      setCurrentPage(page);
      setHasMore(!last);
    } catch (error) {
      console.error('유저 레시피 목록을 가져오는 중 오류 발생:', error);
    } finally {
      isFetching.current = false;
    }
  };

  useEffect(() => {
    loadUserRecipeList(1);
  }, []);

  useEffect(() => {
    if (!observerRef.current) return;

    observerInstance.current = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) {
        loadUserRecipeList(currentPage + PAGE_INDEX.one);
      }
    });

    observerInstance.current.observe(observerRef.current);

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [currentPage, hasMore]);

  return (
    <div className="relative">
      <ul className={PAGE_STYLES.list}>
        {recipeList.length > 0 ? (
          recipeList.map((recipe) => (
            <ContentCard
              type={'user-recipe'}
              data={recipe}
            />
          ))
        ) : (
          <p className="text-center text-gray-500">유저 레시피가 없습니다.</p>
        )}
        <div ref={observerRef}></div>
      </ul>
    </div>
  );
}

export default UserRecipeListContent;
