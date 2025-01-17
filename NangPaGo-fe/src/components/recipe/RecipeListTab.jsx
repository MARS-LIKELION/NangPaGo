function RecipeListTab({ activeTab, setActiveTab, isLoggedIn }) {
  const tabs = [
    { key: 'recommended', label: '추천 레시피' },
    ...(isLoggedIn ? [{ key: 'favorites', label: '즐겨찾기' }] : []),
  ];

  return (
    <div className="flex border-b border-gray-200 top-[10px]">
      {tabs.map((tab) => (
        <button
          key={tab.key}
          className={`flex-1 py-3 text-center font-medium border-b-2 transition-colors duration-200 ${
            activeTab === tab.key
              ? 'border-primary rounded-b-none bg-white text-primary'
              : 'bg-white border-transparent text-text-400 rounded-b-none'
          }`}
          onClick={() => setActiveTab(tab.key)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
}

export default RecipeListTab;
