import { useRefrigerator } from '../../hooks/useRefrigerator';
import { useLocation } from 'react-router-dom';
import Header from '../../components/layout/header/Header';
import IngredientList from '../../components/refrigerator/IngredientList';
import AddIngredientForm from '../../components/refrigerator/AddIngredientForm';
import RecipeCard from '../../components/recipe/RecipeCard';
import TopButton from '../../components/common/TopButton';

function Refrigerator() {
  const {
    ingredients,
    recipes,
    hasMoreRecipes,
    observerRef,
    handleAddIngredient,
    handleDeleteIngredient,
    handleToggleChecked,
    handleSelectAll,
    handleDeselectAll,
    handleFindRecipes,
    resetAndGoBack,
  } = useRefrigerator();

  const hasChecked = ingredients.some((i) => i.checked);
  const location = useLocation();

  return (
    <div className="bg-white shadow-md mx-auto min-h-screen flex flex-col justify-between max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header />
      <div className="flex-grow w-full px-4">
        {location.pathname === '/refrigerator' ? (
          <>
            <div className="mt-4">
              <AddIngredientForm onAdd={handleAddIngredient} />
            </div>
            <div className="mt-6 mb-4">
              <h2 className="text-lg mb-2">내 냉장고</h2>
              <IngredientList
                ingredients={ingredients}
                onDelete={handleDeleteIngredient}
                onToggle={handleToggleChecked}
                onSelectAll={handleSelectAll}
                onDeselectAll={handleDeselectAll}
              />
            </div>
          </>
        ) : (
          <>
            <div className="mt-6 flex-grow flex flex-col">
              <h2 className="text-lg font-medium mb-4">추천 레시피</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {recipes.length > 0 ? (
                  recipes.map((recipe) => (
                    <RecipeCard key={recipe.id} recipe={recipe} />
                  ))
                ) : (
                  <p className="text-center text-gray-500">
                    레시피가 없습니다.
                  </p>
                )}
              </div>
              {hasMoreRecipes && <div ref={observerRef}></div>}
            </div>
          </>
        )}
      </div>
      <div className="sticky bottom-0 bg-white p-4 mt-4 w-full flex justify-center items-center">
        {location.pathname === '/refrigerator' ? (
          <button
            onClick={handleFindRecipes}
            disabled={!hasChecked}
            className={`w-full py-3 text-lg font-medium ${
              hasChecked ? '' : 'bg-yellow-200 text-white cursor-not-allowed'
            }`}
          >
            선택한 재료로 레시피 찾기
          </button>
        ) : (
          <button
            onClick={resetAndGoBack}
            className="w-full py-3 text-lg font-medium "
          >
            돌아가기
          </button>
        )}
      </div>
      <aside className="w-full my-0 mx-auto fixed z-50 left-0 right-0 bottom-0 md:bottom-4 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
        <TopButton />
      </aside>
    </div>
  );
}

export default Refrigerator;
