import { useEffect, useRef } from 'react';
import CookingStepsSlider from './CookingStepsSlider';
import NutritionInfo from './NutritionInfo';
import IngredientList from './IngredientList';
import RecipeImage from './RecipeImage';
import RecipeInfo from './RecipeInfo';
import PostStatusButton from '../button/PostStatusButton';
import usePostStatus from '../../hooks/usePostStatus';
import LoginModal from '../modal/LoginModal';

import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';

function Recipe({ post, data: recipe, isLoggedIn }) {
  const {
    isHeartActive,
    isStarActive,
    likeCount,
    toggleHeart,
    toggleStar,
    modalState,
    setModalState,
  } = usePostStatus(post, isLoggedIn);

  const rightSectionRef = useRef(null);
  const imageRef = useRef(null);
  const sliderRef = useRef(null);

  useEffect(() => {
    const adjustImageHeight = () => {
      if (!rightSectionRef.current || !imageRef.current) {
        return;
      }

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
          <PostStatusButton
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
                <PostStatusButton
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
        <div className="flex justify-end mt-14 sm:scroll-mt-20">
          <span className="text-[7pt] sm:text-[8pt] text-gray-400">
            * 이 레시피는 "
            <a
              href="https://www.foodsafetykorea.go.kr/api/openApiInfo.do?menu_grp=MENU_GRP31&menu_no=661&show_cnt=10&start_idx=1&svc_no=COOKRCP01"
              target="_blank"
              className="text-blue-500 underline"
            >
              식품의약품안전처 - 요리 음식 레시피 DB
            </a>
            "에서 제공된 정보를 바탕으로 작성되었습니다.
          </span>
        </div>
      </section>
      {modalState.type === 'login' && (
        <LoginModal
          isOpen={true}
          onClose={() => setModalState({ type: null, data: null })}
          description={modalState.data}
        />
      )}
    </>
  );
}

export default Recipe;
