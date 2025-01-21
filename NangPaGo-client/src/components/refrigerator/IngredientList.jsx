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

  const allChecked =
    ingredients.length > 0 && ingredients.every((item) => item.checked);

  const buttonLabel = allChecked ? '전체해제' : '전체선택';

  const handleButtonClick = () => {
    if (allChecked) {
      onDeselectAll();
    } else {
      onSelectAll();
    }
  };

  return (
    <div className="relative">
      <button
        onClick={handleButtonClick}
        className="absolute right-0 -top-10 text-white px-3 py-1"
      >
        {buttonLabel}
      </button>

      <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-3">
        {ingredients.map((ingredient) => (
          <li
            key={ingredient.ingredientName}
            className="border border-primary p-2 rounded-md flex justify-between items-center"
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
                  border-text-400
                  text-primary
                  rounded-md
                "
              />
              <span className="ml-2 flex-grow break-words">
                {ingredient.ingredientName}
              </span>
            </label>
            <button
              onClick={() => onDelete(ingredient.ingredientName)}
              className="ml-2 text-sm bg-white text-secondary"
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
