import { useEffect, useRef, useCallback } from 'react';
import CookingStepsSlider from '../userRecipe/CookingStepsSlider';
import PostStatusButton from '../button/PostStatusButton';

const formatDate = (date) =>
  new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(date));

function UserRecipe({ post, data, isLoggedIn }) {
  const rightSectionRef = useRef(null);
  const imageRef = useRef(null);

  const adjustImageHeight = useCallback(() => {
    if (!rightSectionRef.current || !imageRef.current) return;

    if (window.innerWidth > 767) {
      const rightSectionHeight = rightSectionRef.current.offsetHeight;
      imageRef.current.style.height = `${rightSectionHeight}px`;
      imageRef.current.style.objectFit = 'cover';
      return;
    }

    imageRef.current.style.height = 'auto';
  }, []);

  useEffect(() => {
    adjustImageHeight();
    window.addEventListener('resize', adjustImageHeight);

    return () => window.removeEventListener('resize', adjustImageHeight);
  }, [adjustImageHeight]);

  if (!data)
    return (
      <p className="text-center text-gray-500">레시피를 불러오는 중...</p>
    );

  const hasManuals = Array.isArray(data.manuals) && data.manuals.length > 0;

  return (
    <>
      {/* 첫번째 행 */}
      <section className="mt-4 px-4 md:flex md:gap-8 md:items-start">
        {/* 왼쪽 열: 이미지 */}
        <div className="lg:w-7/12 md:w-1/2">
          <div className="overflow-hidden rounded-md flex justify-center items-center bg-gray-50 min-h-[160px]">
            <img
              ref={imageRef}
              src={data.mainImageUrl}
              alt={data.title}
            />
          </div>
        </div>
        {/* 오른쪽 열: 제목, 버튼, 정보 */}
        <div
          className="md:w-5/12 md:flex md:flex-col md:justify-between md:ml-auto"
          ref={rightSectionRef}
        >
          {/* 제목과 상태 버튼 */}
          <div className="flex justify-between items-start mt-4 md:mt-0">
            <h1 className="text-2xl font-bold">{data.title}</h1>
            <PostStatusButton
              post={post}
              isLoggedIn={isLoggedIn}
              className="w-auto"
            />
          </div>

          {/* 작성자 정보 */}
          <div className="text-gray-500 text-sm mt-2">
            <strong className="mr-2">{data.nickname}</strong>
            <span>・</span>
            <span className="ml-2">{formatDate(data.updatedAt)}</span>
          </div>
          <div className="p-4 rounded-lg shadow-sm mt-2">
            <h2 className="text-md font-semibold mb-3">재료</h2>
            <ul className="text-sm list-disc list-inside space-y-2">
              {(data.ingredients || []).map((ingredient, index) => (
                <li key={index} className="text-gray-700 flex justify-between">
                  <span>{ingredient.name}</span>
                  <span className="text-gray-600">{ingredient.amount || "-"}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </section>
      {/* 두번째 행: 레시피 소개 */}
      <section className="mt-4 px-4 md:items-start">
        <div className="mt-6 p-4 bg-gray-50 rounded-lg shadow-sm">
          <h2 className="text-sm font-semibold mb-2">레시피 소개</h2>
          <p className="text-gray-700 font-semibold text-base leading-relaxed">{data.content}</p>
        </div>
      </section>
      {/* 세번째 행: 조리 과정 */}
      <section className="mt-4 px-4 md:items-start p-4">
        <h2 className="text-xl font-semibold mb-4">조리 과정</h2>
        {hasManuals ? (
          <CookingStepsSlider
            manuals={data.manuals.map((manual) => ({
              description: manual.description,
              imageUrl: manual.imageUrl,
            }))}
            manualImages={data.manuals.map((manual) => manual.imageUrl)}
          />
        ) : (
          <p className="text-gray-500">조리 과정 정보가 없습니다.</p>
        )}
      </section>
    </>
  );
}

export default UserRecipe;
