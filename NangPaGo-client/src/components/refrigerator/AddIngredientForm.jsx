import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BiSearch } from 'react-icons/bi';

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

  const getIcon = () => {
    return (
      <BiSearch className="absolute right-3 text-text-400 text-2xl cursor-pointer" />
    );
  };

  return (
    <form onSubmit={handleSubmit}>
      <div
        className="relative flex items-center border border-primary rounded-md bg-white cursor-pointer"
        onClick={handleInputClick}
      >
        <div className={`w-full px-4 py-2 text-text-400`}>
          {'재료 검색...'}
        </div>
        {getIcon()}
      </div>
    </form>
  );
};

export default AddIngredientForm;
