import { FaTimes } from 'react-icons/fa';

const IngredientList = ({ ingredients, onDelete }) => {
  if (!Array.isArray(ingredients)) {
    return null;
  }

  return (
    <ul className="grid grid-cols-2 sm:grid-cols-3 gap-4 mt-3">
      {ingredients.map((ingredient) => {
        const isLongText = ingredient.ingredientName.length >= 5;

        return (
          <li
            key={ingredient.ingredientName}
            className={`border border-[var(--primary-color)] p-2 rounded flex justify-between items-center 
              ${isLongText ? 'col-span-2 sm:col-span-2' : ''}`}
          >
            <span className="flex-grow whitespace-normal break-words">
              {ingredient.ingredientName}
            </span>
            <button
              onClick={() => onDelete(ingredient.ingredientName)}
              className="ml-2"
            >
              <FaTimes />
            </button>
          </li>
        );
      })}
    </ul>
  );
};

export default IngredientList;
