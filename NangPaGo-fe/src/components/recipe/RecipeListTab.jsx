function RecipeListTab({ activeTab, setActiveTab, isLoggedIn }) {
  return (
    <div className="flex border-b border-gray-200 top-[10px]">
      {/* 추천 레시피 탭: 항상 표시 */}
      <button
        className={`flex-1 py-3 text-center font-medium border-b-2 ${
          activeTab === 'recommended'
            ? 'border-[var(--primary-color)] text-[var(--primary-color)]'
            : 'border-transparent text-gray-500 hover:text-gray-700'
        }`}
        onClick={() => setActiveTab('recommended')}
      >
        추천 레시피
      </button>

      {/* 즐겨찾기 탭: 로그인 상태에서만 표시 */}
      {isLoggedIn && (
        <button
          className={`flex-1 py-3 text-center font-medium border-b-2 ${
            activeTab === 'favorites'
              ? 'border-[var(--primary-color)] text-[var(--primary-color)]'
              : 'border-transparent text-gray-500 hover:text-gray-700'
          }`}
          onClick={() => setActiveTab('favorites')}
        >
          즐겨찾기
        </button>
      )}
    </div>
  );
}

export default RecipeListTab;
