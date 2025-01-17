import { FaTimes } from 'react-icons/fa';

const IngredientList = ({
  ingredients,
  onDelete,
  onToggle,
  onSelectAll,
  onDeselectAll,
}) => {
  if (!Array.isArray(ingredients)) {
    return null;
  }

  // 모든 재료가 체크되어 있는지 판단
  const allChecked = ingredients.length > 0 && ingredients.every(item => item.checked);

  // 버튼 라벨
  const buttonLabel = allChecked ? '전체해제' : '전체선택';

  // 버튼 클릭 시 로직
  const handleButtonClick = () => {
    if (allChecked) {
      onDeselectAll();
    } else {
      onSelectAll();
    }
  };

  return (
    <div className="relative">
      {/* 전체선택/전체해제 버튼을 우측 상단에 위치시키기 */}
      <button
        onClick={handleButtonClick}
        className="absolute right-0 -top-10 bg-yellow-400 text-white px-3 py-1 rounded"
      >
        {buttonLabel}
      </button>

      <ul className="grid grid-cols-1 gap-4 mt-3">
        {ingredients.map((ingredient) => (
          <li
            key={ingredient.ingredientName}
            className="border border-[var(--primary-color)] p-2 rounded flex justify-between items-center"
          >
            <label className="flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={ingredient.checked || false}
                onChange={() => onToggle(ingredient.ingredientName)}
                className="
                  form-checkbox
                  h-5 w-5
                  border-2
                  border-gray-300
                  text-[var(--primary-color)]
                  rounded
                  focus:ring-2
                  focus:ring-[var(--primary-color)]
                  focus:ring-offset-2
                "
              />
              <span className="ml-2 flex-grow break-words">
                {ingredient.ingredientName}
              </span>
            </label>
            <button
              onClick={() => onDelete(ingredient.ingredientName)}
              className="ml-2 text-sm text-gray-500"
            >
              <FaTimes />
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default IngredientList;
