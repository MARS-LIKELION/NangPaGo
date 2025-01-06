import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AddIngredientForm = ({ onAdd }) => {
  const [ingredientName, setIngredientName] = useState('');
  const navigate = useNavigate();

  // 입력 창 클릭 시, 검색 페이지로 이동
  const handleInputClick = () => {
    navigate('/refrigerator/search');
  };

  // 입력 값 변경 핸들러
  const handleChange = (e) => {
    setIngredientName(e.target.value);
  };

  // 폼 submit 핸들러 (재료 추가)
  const handleSubmit = (e) => {
    e.preventDefault();

    // 빈 문자열 방지
    if (!ingredientName.trim()) return;

    // 부모 컴포넌트에서 받은 onAdd 함수 실행
    onAdd(ingredientName);

    // 입력창 초기화
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
        className="border border-[var(--primary-color)] p-2 rounded flex-grow"
      />
    </form>
  );
};

export default AddIngredientForm;
