import { useEffect, useRef } from 'react';
import { useSelector } from 'react-redux';
import LoginModal from '../modal/LoginModal';
import CookingStepsSlider from './CookingStepsSlider';
import NutritionInfo from './NutritionInfo';
import IngredientList from './IngredientList';
import RecipeImage from './RecipeImage';
import RecipeInfo from './RecipeInfo';
import RecipeButton from '../button/RecipeButton';

import useRecipeData from '../../hooks/useRecipeData';

import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

function Recipe({ data: recipe }) {
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

  const rightSectionRef = useRef(null);
  const imageRef = useRef(null);
  const sliderRef = useRef(null);

  useEffect(() => {
    const adjustImageHeight = () => {
      if (!rightSectionRef.current || !imageRef.current) return;

      if (window.innerWidth > 767) {
        const rightSectionHeight = rightSectionRef.current.offsetHeight;
        imageRef.current.style.height = `${rightSectionHeight}px`;
        imageRef.current.style.objectFit = 'cover';
        return;
      }

      imageRef.current.style.height = 'auto';
    };

    adjustImageHeight();
    window.addEventListener('resize', adjustImageHeight);

    return () => window.removeEventListener('resize', adjustImageHeight);
  }, []);

  useEffect(() => {
    const resetSlider = () => {
      if (sliderRef.current?.innerSlider) {
        sliderRef.current.innerSlider.slickGoTo(0);
      }
    };

    window.addEventListener('resize', resetSlider);

    return () => window.removeEventListener('resize', resetSlider);
  }, []);

  return (
    <>
      <section className="mt-4 px-4 md:flex md:gap-8 md:items-start">
        <RecipeImage
          imageRef={imageRef}
          mainImage={recipe.mainImage}
          recipeName={recipe.name}
        />
        <div className="mt-4 md:hidden">
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
          className="md:w-1/2 md:flex md:flex-col md:justify-between"
          ref={rightSectionRef}
        >
          <div>
            <div className="flex flex-col md:flex-row md:items-start md:justify-between mt-4 md:mt-0">
              <RecipeInfo recipe={recipe} />
              <div className="hidden md:flex items-center gap-4">
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

          <div className="mt-7 flex flex-col md:gap-4">
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
          ref={sliderRef}
          manuals={recipe.manuals}
          manualImages={recipe.manualImages}
        />
      </section>
      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowDeleteModal(false)}
      />
    </>
  );
}

export default Recipe;
