import { useEffect, useRef, useCallback } from 'react';
import CookingStepsSlider from './CookingStepsSlider';
import NutritionInfo from './NutritionInfo';
import IngredientList from './IngredientList';
import RecipeImage from './RecipeImage';
import RecipeInfo from './RecipeInfo';
import PostStatusButton from '../button/PostStatusButton';

function Recipe({ post, data: recipe, isLoggedIn }) {
  const rightSectionRef = useRef(null);
  const imageRef = useRef(null);
  const sliderRef = useRef(null);

  const adjustImageHeight = useCallback(() => {
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
  }, []);

  useEffect(() => {
    adjustImageHeight();
    window.addEventListener('resize', adjustImageHeight);

    return () => window.removeEventListener('resize', adjustImageHeight);
  }, [adjustImageHeight]);


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
        <div
          className="md:w-5/12 md:flex md:flex-col md:justify-between md:ml-auto"
          ref={rightSectionRef}
        >
          <div>
            <div className="flex flex-col md:flex-row md:items-start md:justify-between mt-4 md:mt-0">
              <RecipeInfo recipe={recipe} />
              <div className="flex items-center gap-4 w-full mt-4 md:mt-0 md:w-auto">
                <PostStatusButton post={post} isLoggedIn={isLoggedIn} className="w-full md:w-auto" />
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
    </>
  );
}

export default Recipe;
