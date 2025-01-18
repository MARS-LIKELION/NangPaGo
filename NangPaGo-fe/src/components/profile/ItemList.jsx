import { useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const ItemList = ({ items, activeTab, hasMore, onLoadMore, isLoading }) => {
  const observerRef = useRef(null);
  const observerInstance = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasMore && !isLoading) {
          onLoadMore();
        }
      },
      { threshold: 1.0 },
    );

    if (observerRef.current) {
      observerInstance.current.observe(observerRef.current);
    }

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [hasMore, onLoadMore, isLoading]);

  if (isLoading && items.length === 0) {
    return (
      <div className="flex justify-center items-center h-96">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-primary"></div>
      </div>
    );
  }

  if (!items.length && !hasMore) {
    return <div className="text-center py-4">항목이 없습니다.</div>;
  }

  return (
    <div
      className={`${
        activeTab === 'comments'
          ? 'flex flex-col gap-2'
          : 'grid sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'
      } min-h-[300px]`}
    >
      {items.map((item) =>
        activeTab === 'comments' ? (
          <div key={item.id} className="flex gap-5 border-b pb-1">
            <div className="w-[100px] h-[130px]">
              {/* 레시피 이미지와 제목 클릭 시 이동 */}
              <div
                className="w-[100px] h-[100px] overflow-hidden rounded-md border mb-2 cursor-pointer"
                onClick={() => navigate(`/recipe/${item.id}`)} // 이동
              >
                <img
                  src={item.imageUrl}
                  alt={item.title}
                  className="w-full h-full object-cover"
                />
              </div>
              <p
                className="line-clamp-1 text-sm cursor-pointer"
                onClick={() => navigate(`/recipe/${item.id}`)} // 이동
              >
                {item.title}
              </p>
            </div>
            <div>
              <h3 className="text-text-900 text-base line-clamp-4">
                {item.content}
              </h3>
            </div>
          </div>
        ) : (
          <Link
            key={item.id}
            to={`/recipe/${item.id}`}
            className="block overflow-hidden rounded-md shadow-md hover:shadow-xl transition-shadow duration-300"
          >
            <img
              src={item.recipeImageUrl || item.mainImage || ''}
              alt={item.name}
              className="w-full h-48 object-cover"
            />
            <div className="p-4 flex flex-col gap-2">
              <h3 className="text-lg">{item.name}</h3>
              <div className="flex flex-row gap-2 text-sm">
                {item.cookingMethod && (
                  <span className="py-1 px-2 bg-secondary rounded-md flex-row">
                    {item.cookingMethod}
                  </span>
                )}

                {item.category && (
                  <span className="py-1 px-2 bg-secondary rounded-md">
                    {item.category}
                  </span>
                )}

                {item.mainIngredient && (
                  <span className="py-1 px-2 bg-secondary rounded-md">
                    {item.mainIngredient}
                  </span>
                )}
              </div>
              <div className="flex flex-row gap-2">
                {item.ingredientsDisplayTag?.map((tag, idx) => (
                  <span
                    key={idx}
                    className="text-sm py-1 px-2 rounded-md bg-secondary text-text-900 "
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          </Link>
        ),
      )}
      {hasMore && <div ref={observerRef} style={{ height: '1px' }} />}
    </div>
  );
};

export default ItemList;
