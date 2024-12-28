import { useState } from 'react';
import { useSelector } from 'react-redux';
import IngredientList from './IngredientList';
import CookingSteps from './CookingSteps';
import NutritionInfo from './NutritionInfo';
import Header from '../common/Header';
import Footer from '../common/Footer';
import { FaHeart, FaStar, FaTimes } from 'react-icons/fa';

function Recipe({ recipe }) {
  const { email: userEmail } = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(userEmail);
  const [isHeartActive, setIsHeartActive] = useState(false);
  const [isStarActive, setIsStarActive] = useState(false);
  const [comment, setComment] = useState('');
  const [comments, setComments] = useState([]);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const toggleHeart = () => {
    if (isLoggedIn) {
      setIsHeartActive(!isHeartActive);
    } else {
      setShowLoginModal(true);
    }
  };

  const toggleStar = () => {
    if (isLoggedIn) {
      setIsStarActive(!isStarActive);
    } else {
      setShowLoginModal(true);
    }
  };

  const handleCommentChange = (e) => setComment(e.target.value);

  const handleCommentSubmit = () => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (comment.trim()) {
      const maskedEmail = `${userEmail.slice(0, 3)}***`;
      setComments([...comments, { user: maskedEmail, text: comment }]);
      setComment('');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleCommentSubmit();
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col justify-between">
      <div>
        <Header />
        <div className="mt-4 mx-4">
          <img
            src={recipe.mainImage}
            alt={recipe.name}
            className="w-full h-48 object-cover rounded-md"
          />
        </div>
        <div className="mt-4 mx-4 flex items-center justify-between">
          <h1 className="text-xl font-bold">{recipe.name}</h1>
          <div className="flex gap-2">
            <button
              className={`bg-white ${
                isHeartActive ? 'text-red-500' : 'text-gray-500'
              }`}
              onClick={toggleHeart}
            >
              <FaHeart className="bg-white text-2xl" />
            </button>
            <button
              className={`bg-white ${
                isStarActive ? 'text-yellow-500' : 'text-gray-500'
              }`}
              onClick={toggleStar}
            >
              <FaStar className="bg-white text-2xl" />
            </button>
          </div>
        </div>
        <div className="mx-4">
          <p className="text-gray-600 text-[14px] mt-2">{recipe.category}</p>
        </div>
        <div className="mt-7 mx-4">
          <h2 className="text-lg font-semibold mb-3">재료</h2>
          <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
            {recipe.ingredients
              .split('소스')[0]
              .split(/,|\n/)
              .map((ingredient, index) => (
                <li key={`ingredient-${index}`} className="font-medium">
                  {ingredient.replace(/[^\w\s가-힣.]/gi, ' ').trim()}
                </li>
              ))}
          </ul>
        </div>
        {recipe.ingredients.includes('소스') && (
          <div className="mt-7 mx-4">
            <h2 className="text-lg font-semibold mb-3">소스</h2>
            <ul className="grid grid-cols-2 gap-x-4 gap-y-2 text-gray-700 text-sm">
              {recipe.ingredients
                .split('소스')[1]
                .split(/,|\n/)
                .map((sauce, index) => (
                  <li key={`sauce-${index}`} className="font-medium">
                    {sauce.replace(/[^\w\s가-힣.]/gi, ' ').trim()}
                  </li>
                ))}
            </ul>
          </div>
        )}
        <div className="mt-7 mx-4">
          <h2 className="text-lg font-semibold">요리 과정</h2>
          {recipe.manuals.map((step, index) => (
            <div key={index} className="mt-4">
              <CookingSteps
                steps={[step]}
                stepImages={[recipe.manualImages[index]]}
              />
            </div>
          ))}
        </div>
        <div className="mt-7 mx-4">
          <NutritionInfo
            calories={recipe.calories}
            fat={recipe.fat}
            carbs={recipe.carbohydrates}
            protein={recipe.protein}
            sodium={recipe.sodium}
          />
        </div>
        <div className="mt-7 mx-4">
          <h2 className="text-lg font-semibold">댓글</h2>
          <textarea
            value={comment}
            onChange={handleCommentChange}
            onKeyPress={handleKeyPress}
            className="w-full p-2 border border-gray-300 rounded-md"
            placeholder={
              isLoggedIn
                ? '댓글을 입력하세요...'
                : '로그인 후 댓글을 남겨주세요.'
            }
            disabled={!isLoggedIn}
          />
          <button
            onClick={handleCommentSubmit}
            className="mt-2 bg-[var(--primary-color)] text-white px-4 py-2 rounded-md"
          >
            전송
          </button>
          {showLoginModal && (
            <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
              <div className="bg-white p-8 rounded-lg relative flex flex-col items-center w-[calc(100% - 32px)] max-w-[400px]">
                <button
                  onClick={() => setShowLoginModal(false)}
                  className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                >
                  <FaTimes className="w-6 h-6" />
                </button>
                <p className="text-center">로그인 하시겠습니까?</p>
                <button
                  onClick={() => (window.location.href = '/login')}
                  className="mt-4 bg-[var(--primary-color)] text-white px-5 py-3 rounded-lg"
                >
                  로그인
                </button>
              </div>
            </div>
          )}
          <div className="mt-4">
            {comments.map((cmt, index) => (
              <p key={index} className="text-gray-700 text-sm mt-2">
                <strong>{cmt.user}</strong>: {cmt.text}
              </p>
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default Recipe;
