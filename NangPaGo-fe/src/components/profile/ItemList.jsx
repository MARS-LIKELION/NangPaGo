import { useRef, useEffect } from 'react';
import clsx from 'clsx';

const ItemList = ({ items, activeTab, hasMore, onLoadMore, onItemClick }) => {
  const observerRef = useRef(null);
  const observerInstance = useRef(null);

  useEffect(() => {
    if (observerInstance.current) observerInstance.current.disconnect();

    observerInstance.current = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasMore) {
          onLoadMore();
        }
      },
      { threshold: 0.1 },
    );

    if (observerRef.current) {
      observerInstance.current.observe(observerRef.current);
    }

    return () => {
      if (observerInstance.current) observerInstance.current.disconnect();
    };
  }, [hasMore, onLoadMore]);

  if (!items.length && !hasMore) {
    return <div>항목이 없습니다.</div>;
  }

  return (
    <div className="grid grid-cols-1 gap-4">
      {items.map((item) => (
        <div
          key={item.id}
          className={clsx(
            'flex flex-col h-auto overflow-hidden border rounded-lg shadow-sm',
            {
              'p-4 bg-gray-100': activeTab === 'comments', // 댓글: 회색 배경과 커서 기본값
              'p-0 cursor-pointer': activeTab !== 'comments', // 다른 탭: 커서 포인터
            },
          )}
          onClick={() => {
            if (activeTab !== 'comments') {
              onItemClick(item.id);
            }
          }}
        >
          {activeTab === 'comments' ? (
            <>
              <p className="text-gray-800 mb-2">{item.content}</p>
              <small className="text-gray-700">
                작성일: {new Date(item.updatedAt).toLocaleString()}
              </small>
            </>
          ) : (
            <div className="flex h-[90px]">
              <div className="w-[90px] h-[90px] flex-shrink-0 bg-gray-100 overflow-hidden">
                <img
                  src={item.mainImage || ''}
                  alt={item.name || item.content}
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.target.onerror = null;
                  }}
                />
              </div>

              <div className="flex flex-col justify-between p-3 flex-grow">
                <h3 className="font-bold mb-2 line-clamp-1 text-black">
                  {item.name}
                </h3>

                <div className="text-sm text-black flex flex-wrap gap-2">
                  {item.cookingMethod && (
                    <span className="py-1 px-2 bg-[var(--secondary-color)] rounded">
                      {item.cookingMethod}
                    </span>
                  )}

                  {item.category && (
                    <span className="py-1 px-2 bg-[var(--secondary-color)] rounded">
                      {item.category}
                    </span>
                  )}

                  {item.mainIngredient && (
                    <span className="py-1 px-2 bg-[var(--secondary-color)] rounded">
                      {item.mainIngredient}
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      ))}
      {hasMore && <div ref={observerRef} style={{ height: '1px' }} />}
    </div>
  );
};

export default ItemList;
