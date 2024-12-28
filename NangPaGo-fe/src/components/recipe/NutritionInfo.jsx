function NutritionInfo({ calories, fat, carbs, protein, sodium }) {
  return (
    <div className="nutrition-info mt-4">
      <h2 className="text-lg font-semibold mb-3">영양 정보</h2>
      <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
        <li>
          칼로리 <span className="font-medium">{calories}kcal</span>
        </li>
        <li>
          지방 <span className="font-medium">{fat}g</span>
        </li>
        <li>
          탄수화물 <span className="font-medium">{carbs}g</span>
        </li>
        <li>
          단백질 <span className="font-medium">{protein}g</span>
        </li>
        <li className="col-span-2">
          나트륨 <span className="font-medium">{sodium}mg</span>
        </li>
      </ul>
    </div>
  );
}

export default NutritionInfo;
