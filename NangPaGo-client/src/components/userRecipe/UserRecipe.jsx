import CookingStepsSlider from '../userRecipe/CookingStepsSlider';
import PostStatusButton from '../button/PostStatusButton';

const formatDate = (date) =>
  new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(date));

function UserRecipe({ post, data, isLoggedIn }) {
  if (!data) return <p className="text-center text-gray-500">레시피를 불러오는 중...</p>; // TODO: LoadingSpinner로 통일

  const hasManuals = Array.isArray(data.manuals) && data.manuals.length > 0;

  return (
    <div className="max-w-4xl mx-auto bg-white shadow-md rounded-lg p-6 relative">
      <div className="md:flex md:items-start gap-8">
        {/* 좌측 열: 대표이미지만 표시 */}
        <div className="md:w-1/2">
          <img
            src={data.mainImageUrl}
            alt={data.title}
            className="w-full max-h-80 object-cover rounded-2xl shadow-md mb-6"
          />
        </div>

        {/* 우측 열: 제목, 내용, 재료 등 */}
        <div className="md:w-1/2">
          <div className="flex justify-between items-center mt-6 relative">
            <h1 className="text-2xl font-bold">{data.title}</h1>
            <PostStatusButton
              post={post}
              isLoggedIn={isLoggedIn}
            />
          </div>
          <div className="mt-2 text-gray-500 text-xs">
            <strong className="mr-2">{data.nickname}</strong>
            <span>・</span>
            <span className="ml-2">{formatDate(data.updatedAt)}</span>
          </div>
          <p className="text-gray-700 mt-4">{data.content}</p>

          <h2 className="text-lg font-semibold mt-6 mb-2">재료</h2>
          <ul className="list-disc list-inside space-y-2">
            {(data.ingredients || []).map((ingredient, index) => (
              <li key={index} className="text-gray-700 flex justify-between">
                <span className="font-semibold">{ingredient.name}</span>
                <span className="ml-6">{ingredient.amount}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* 조리 과정 영역 – 모든 메뉴얼(조리 과정)을 슬라이더로 표시 */}
      <div className="mt-8">
        <h2 className="text-lg font-semibold mb-2">조리 과정</h2>
        {hasManuals ? (
          <CookingStepsSlider
            manuals={data.manuals.map((manual) => ({
              description: manual.description,
              imageUrl: manual.imageUrl,
            }))}
            manualImages={data.manuals.map((manual) => manual.imageUrl)}
            isUserRecipe={true}
          />
        ) : (
          <p className="text-gray-500">조리 과정 정보가 없습니다.</p>
        )}
      </div>
    </div>
  );
}

export default UserRecipe;
