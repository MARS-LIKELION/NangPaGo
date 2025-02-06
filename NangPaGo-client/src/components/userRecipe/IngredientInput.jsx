import { motion, AnimatePresence } from 'framer-motion';
import { X } from 'lucide-react';

function IngredientInput({ ingredients, setIngredients }) {
  const addIngredient = () => {
    setIngredients([...ingredients, { name: '', amount: '' }]);
  };

  const removeIngredient = (index) => {
    const updatedIngredients = ingredients.filter((_, i) => i !== index);
    setIngredients(updatedIngredients);
  };

  const updateIngredient = (index, field, value) => {
    const updatedIngredients = [...ingredients];
    if (typeof updatedIngredients[index] === 'string') {
      updatedIngredients[index] = { name: updatedIngredients[index], amount: '' };
    }
    updatedIngredients[index][field] = value;
    setIngredients(updatedIngredients);
  };

  return (
    <div className="mt-4">
      <h2 className="text-xl font-semibold mb-2">재료</h2>
      <AnimatePresence>
        {ingredients.map((ingredient, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
            className="flex items-center gap-3 mt-2 p-3 border border-gray-200 rounded-lg bg-white shadow-sm"
          >
            <input
              type="text"
              className="flex-1 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-400"
              placeholder="재료 입력"
              value={ingredient.name || ''}
              onChange={(e) => updateIngredient(index, 'name', e.target.value)}
            />
            <input
              type="text"
              className="w-1/4 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-400"
              placeholder="수량"
              value={ingredient.amount || ''}
              onChange={(e) => updateIngredient(index, 'amount', e.target.value)}
            />
            {ingredients.length > 1 && (
              <motion.button
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.9 }}
                onClick={() => removeIngredient(index)}
                className="w-8 h-8 flex items-center justify-center rounded-full bg-gray-300 hover:bg-gray-400 transition-all"
              >
                <X className="w-4 h-4 text-gray-600" />
              </motion.button>
            )}
          </motion.div>
        ))}
      </AnimatePresence>
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={addIngredient}
        className="mt-4 w-full p-2 bg-green-500 text-white font-semibold rounded-lg shadow-md transition-all duration-300"
      >
        + 재료 추가
      </motion.button>
    </div>
  );
}

export default IngredientInput;
