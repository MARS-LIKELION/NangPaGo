function IngredientList({ ingredients }) {
  return (
    <div className="ingredient-list mt-4">
      <h2 className="text-lg font-semibold">재료</h2>
      <p className="text-gray-700 text-sm whitespace-pre-line">{ingredients}</p>
    </div>
  );
}

export default IngredientList;
