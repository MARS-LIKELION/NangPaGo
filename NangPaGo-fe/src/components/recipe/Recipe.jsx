import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

import Header from '../layout/header/Header';
import Footer from '../common/Footer';
import LoginModal from '../../common/modal/LoginModal';
import RecipeComment from './comment/RecipeComment';
import CookingStepsSlider from './CookingStepsSlider';
import NutritionInfo from './NutritionInfo';
import IngredientList from './IngredientList';
import RecipeImage from './RecipeImage';
import RecipeInfo from './RecipeInfo';
import RecipeButton from './RecipeButton';

import useRecipeData from '../../hooks/useRecipeData';

import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

function Recipe({ recipe }) {
  const { email: userEmail } = useSelector((state) => state.loginSlice);
  const isLoggedIn = Boolean(userEmail);

  const {
    isHeartActive,
    isStarActive,
    likeCount,
    showLoginModal,
    toggleHeart,
    toggleStar,
    setShowLoginModal,
  } = useRecipeData(recipe.id, isLoggedIn);

  const navigate = useNavigate();
  const rightSectionRef = useRef(null);
  const imageRef = useRef(null);

  useEffect(() => {
    const adjustImageHeight = () => {
      if (
        window.innerWidth > 1024 &&
        rightSectionRef.current &&
        imageRef.current
      ) {
        const rightSectionHeight = rightSectionRef.current.offsetHeight;
        imageRef.current.style.height = `${rightSectionHeight}px`;
        imageRef.current.style.objectFit = 'cover';
      } else if (imageRef.current) {
        imageRef.current.style.height = 'auto';
      }
    };

    adjustImageHeight();
    window.addEventListener('resize', adjustImageHeight);

    return () => window.removeEventListener('resize', adjustImageHeight);
  }, []);

  const closeModal = () => setShowLoginModal(false);
  const navigateToLogin = () => {
    setShowLoginModal(false);
    navigate('/login');
  };

  return (
    <div className="bg-white shadow-md mx-auto min-h-screen flex flex-col justify-between min-w-80 max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header />

      <main>
        <section className="mt-4 px-4 lg:flex lg:gap-8 lg:items-start">
          <RecipeImage
            imageRef={imageRef}
            mainImage={recipe.mainImage}
            recipeName={recipe.name}
          />
          <div className="mt-4 lg:hidden">
            <RecipeButton
              isHeartActive={isHeartActive}
              isStarActive={isStarActive}
              likeCount={likeCount}
              toggleHeart={toggleHeart}
              toggleStar={toggleStar}
              className="w-full"
            />
          </div>

          <div
            className="lg:w-1/2 lg:flex lg:flex-col lg:justify-between"
            ref={rightSectionRef}
          >
            <div>
              <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between mt-4 lg:mt-0">
                <RecipeInfo recipe={recipe} />
                <div className="hidden lg:flex items-center gap-4">
                  <RecipeButton
                    isHeartActive={isHeartActive}
                    isStarActive={isStarActive}
                    likeCount={likeCount}
                    toggleHeart={toggleHeart}
                    toggleStar={toggleStar}
                  />
                </div>
              </div>
            </div>

            <div className="mt-7 flex flex-col lg:gap-4">
              <IngredientList ingredients={recipe.ingredients} />
              <NutritionInfo
                calories={recipe.calorie}
                fat={recipe.fat}
                carbs={recipe.carbohydrate}
                protein={recipe.protein}
                sodium={recipe.natrium}
              />
            </div>
          </div>
        </section>

        <section className="mt-7 px-4">
          <h2 className="text-lg font-semibold">요리 과정</h2>
          <CookingStepsSlider
            manuals={recipe.manuals}
            manualImages={recipe.manualImages}
          />
        </section>

        <RecipeComment recipeId={recipe.id} />
      </main>

      <Footer />
      <LoginModal
        isOpen={showLoginModal}
        onConfirm={navigateToLogin}
        onClose={closeModal}
      />
    </div>
  );
}

export default Recipe;
