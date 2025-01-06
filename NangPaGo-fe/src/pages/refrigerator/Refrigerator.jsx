import { useEffect, useState } from 'react';
import {
  getRefrigerator,
  addIngredient,
  deleteIngredient,
} from '../../api/refrigerator';
import Header from '../../components/common/Header';
import IngredientList from '../../components/refirgerator/IngredientList';
import AddIngredientForm from '../../components/refirgerator/AddIngredientForm';

const Refrigerator = () => {
  const [ingredients, setIngredients] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  useEffect(() => {
    fetchRefrigerator(page, size);
  }, [page, size]);

  const fetchRefrigerator = async (page, size) => {
    try {
      const data = await getRefrigerator(page, size);
      setIngredients(data);
    } catch (error) {
      console.error('냉장고 데이터를 가져오는 데 실패했습니다.', error);
      setIngredients([]);
    }
  };

  const handleAddIngredient = async (ingredientName) => {
    try {
      const addedIngredient = await addIngredient(ingredientName);
      setIngredients((prev) => [...prev, addedIngredient]);
    } catch (error) {
      console.error('재료 추가 중 오류가 발생했습니다.', error);
    }
  };

  const handleDeleteIngredient = async (ingredientName) => {
    try {
      const message = await deleteIngredient(ingredientName);
      console.log(message);
      setIngredients((prev) =>
        prev.filter((item) => item.ingredientName !== ingredientName),
      );
    } catch (error) {
      console.error('재료 삭제 중 오류가 발생했습니다.', error);
    }
  };

  return (
    <div className="bg-white mx-auto w-[375px] min-h-screen flex flex-col items-center">
      <Header title="냉장고 파먹기" />

      <div className="w-full px-4 mt-4">
        <AddIngredientForm onAdd={handleAddIngredient} />
      </div>

      <div className="w-full px-4 mt-6 mb-4">
        <h2 className="text-lg font-medium mb-2">내 냉장고</h2>
        <IngredientList
          ingredients={ingredients}
          onDelete={handleDeleteIngredient}
        />
      </div>

      <div className="w-full px-4 mt-auto mb-4">
        <button
          className="bg-[var(--primary-color)] text-white w-full py-3 rounded-lg text-lg font-medium"
          onClick={() => console.log('냉파고 레시피 찾기 클릭')}
        >
          냉파고 레시피 찾기
        </button>
      </div>
    </div>
  );
};

export default Refrigerator;
