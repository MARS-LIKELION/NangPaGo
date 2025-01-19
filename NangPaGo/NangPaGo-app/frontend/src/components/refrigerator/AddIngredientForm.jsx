import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AddIngredientForm = ({ onAdd }) => {
  const [ingredientName, setIngredientName] = useState('');
  const navigate = useNavigate();

  const handleInputClick = () => {
    navigate('/refrigerator/search');
  };

  const handleChange = (e) => {
    setIngredientName(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!ingredientName.trim()) return;
    onAdd(ingredientName);
    setIngredientName('');
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-center gap-2">
      <input
        type="text"
        value={ingredientName}
        onChange={handleChange}
        onClick={handleInputClick}
        placeholder="재료 이름 입력"
        className="border border-primary p-2 rounded-md flex-grow text-text-400"
      />
    </form>
  );
};

export default AddIngredientForm;
