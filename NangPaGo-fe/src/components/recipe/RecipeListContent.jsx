import RecipeCard from './RecipeCard';

function RecipeListContent({ activeTab, images, searchTerm }) {
  const filteredImages = images.filter((image) =>
    image.title.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  return (
    <div className="grid grid-cols-1 gap-6 min-h-[400px]">
      {activeTab === 'recommended' ? (
        filteredImages.length > 0 ? (
          filteredImages.map((image) => (
            <RecipeCard key={image.id} image={image} />
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            검색 결과가 없습니다.
          </div>
        )
      ) : (
        <div className="text-center py-8 text-gray-500">
          즐겨찾기한 레시피가 없습니다.
        </div>
      )}
    </div>
  );
}

export default RecipeListContent;
