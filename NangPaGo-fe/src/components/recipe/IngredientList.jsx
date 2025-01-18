function IngredientList({ ingredients }) {
  return (
    <div className="ingredient-list mt-4">
      <h2 className="text-lg font-semibold mb-3">재료</h2>
      <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
        {ingredients.split(',').map((ingredient, index) => (
          <li key={index} className="font-medium">
            {ingredient.replace(/[^가-힣a-zA-Z0-9()./×\s]/gi, '').trim()}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default IngredientList;
