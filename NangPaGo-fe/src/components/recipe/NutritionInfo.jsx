function NutritionInfo({ calories, fat, carbs, protein, sodium }) {
  return (
    <div className="nutrition-info mt-4">
      <h2 className="text-lg font-semibold">영양 정보</h2>
      <ul className="text-gray-700 text-sm">
        <li>칼로리: {calories}kcal</li>
        <li>지방: {fat}g</li>
        <li>탄수화물: {carbs}g</li>
        <li>단백질: {protein}g</li>
        <li>나트륨: {sodium}mg</li>
      </ul>
    </div>
  );
}

export default NutritionInfo;
