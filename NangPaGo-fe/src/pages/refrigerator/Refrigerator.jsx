import { useEffect, useState, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  getRefrigerator,
  addIngredient,
  deleteIngredient,
} from '../../api/refrigerator';
import { getRecipes } from '../../api/recipe.js';
import Header from '../../components/common/Header';
import IngredientList from '../../components/refrigerator/IngredientList';
import AddIngredientForm from '../../components/refrigerator/AddIngredientForm';
import RecipeCard from '../../components/recipe/RecipeCard';
import TopButton from '../../components/common/TopButton.jsx';

function Refrigerator() {
  const [ingredients, setIngredients] = useState([]);
  const [recipes, setRecipes] = useState(
    () => JSON.parse(localStorage.getItem('recipes')) || [],
  );
  const [recipePage, setRecipePage] = useState(
    () => parseInt(localStorage.getItem('recipePage'), 10) || 1,
  );
  const [recipeSize] = useState(10);
  const [hasMoreRecipes, setHasMoreRecipes] = useState(
    () => localStorage.getItem('hasMoreRecipes') === 'true',
  );
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const location = useLocation();
  const observerRef = useRef(null);

  // 첫 useEffect(마운트 직후)에서 중복 호출을 막기 위한 ref
  const isMounted = useRef(false);

  /**
   * 1) 컴포넌트 최초 마운트 시, 냉장고 재료 목록 API 호출
   */
  useEffect(() => {
    fetchRefrigerator();
  }, []);

  /**
   * 2) 뒤로가기("/refrigerator/recipe") 또는 재료(ingredients) 변동 시 재조회
   *    - 단, "처음 마운트" 이후부터만 작동(중복 호출 방지)
   */
  useEffect(() => {
    if (!isMounted.current) {
      // 첫 렌더는 스킵
      isMounted.current = true;
      return;
    }

    if (location.pathname === '/refrigerator/recipe') {
      reFetchRefrigeratorRecipes();
    }
  }, [location.pathname, ingredients]);

  /**
   * 3) recipes, recipePage 등이 바뀔 때마다 로컬 스토리지 동기화
   */
  useEffect(() => {
    syncLocalStorage();
  }, [recipes, recipePage, hasMoreRecipes]);

  /**
   * 4) 무한 스크롤 IntersectionObserver 설정
   */
  useEffect(() => {
    if (location.pathname === '/refrigerator/recipe' && hasMoreRecipes) {
      const observer = new IntersectionObserver(handleObserver, {
        threshold: 1.0,
      });
      if (observerRef.current) observer.observe(observerRef.current);
      return () => observer.disconnect();
    }
  }, [location.pathname, recipes, hasMoreRecipes]);

  /** 로컬 스토리지 동기화 */
  const syncLocalStorage = () => {
    localStorage.setItem('recipes', JSON.stringify(recipes));
    localStorage.setItem('recipePage', recipePage.toString());
    localStorage.setItem('hasMoreRecipes', hasMoreRecipes.toString());
  };

  /** 에러 처리 */
  const handleApiError = (message, error) => {
    console.error(message, error);
  };

  /** (1) 냉장고 재료 불러오기 */
  const fetchRefrigerator = async () => {
    try {
      const data = await getRefrigerator();
      setIngredients(data);
    } catch (error) {
      handleApiError('냉장고 데이터를 가져오는 데 실패했습니다.', error);
      setIngredients([]);
    }
  };

  /**
   * (2) 뒤로가기 시 재조회 (마운트 후부터)
   */
  const reFetchRefrigeratorRecipes = async () => {
    setRecipePage(1);
    setHasMoreRecipes(true);

    try {
      const ingredientNames = ingredients
        .map((i) => i.ingredientName)
        .filter(Boolean);

      const recipeData = await getRecipes(ingredientNames, 1, recipeSize);
      setRecipes(recipeData.content);
      setHasMoreRecipes(!recipeData.last);
    } catch (error) {
      handleApiError('레시피를 다시 가져오는 중 오류가 발생했습니다.', error);
    }
  };

  /**
   * 재료 추가
   */
  const handleAddIngredient = async (ingredientName) => {
    try {
      const addedIngredient = await addIngredient(ingredientName);
      setIngredients((prev) => [...prev, addedIngredient]);
    } catch (error) {
      handleApiError('재료 추가 중 오류가 발생했습니다.', error);
    }
  };

  /**
   * 재료 삭제
   */
  const handleDeleteIngredient = async (ingredientName) => {
    try {
      await deleteIngredient(ingredientName);
      setIngredients((prev) =>
        prev.filter((item) => item.ingredientName !== ingredientName),
      );
    } catch (error) {
      handleApiError('재료 삭제 중 오류가 발생했습니다.', error);
    }
  };

  /**
   * (3) "레시피 찾기" 버튼 클릭
   * - 그래도 재료가 없으면 함수 초반에 return
   */
  const handleFindRecipes = async () => {
    const ingredientNames = ingredients
      .map((i) => i.ingredientName)
      .filter(Boolean);
    if (ingredientNames.length === 0) {
      return; // 안전장치
    }

    setRecipePage(1);
    setHasMoreRecipes(true);

    try {
      const recipeData = await getRecipes(ingredientNames, 1, recipeSize);
      setRecipes(recipeData.content);
      setHasMoreRecipes(!recipeData.last);

      // 레시피 페이지로 이동
      navigate('/refrigerator/recipe');
    } catch (error) {
      handleApiError('레시피를 가져오는 중 오류가 발생했습니다.', error);
    }
  };

  /**
   * 무한 스크롤 로딩
   */
  const loadMoreRecipes = async () => {
    if (!hasMoreRecipes || isLoading) return;
    setIsLoading(true);

    const nextPage = recipePage + 1;
    try {
      const ingredientNames = ingredients
        .map((i) => i.ingredientName)
        .filter(Boolean);

      const recipeData = await getRecipes(ingredientNames, nextPage, recipeSize);
      setRecipes((prev) => [...prev, ...recipeData.content]);
      setRecipePage(nextPage);
      setHasMoreRecipes(!recipeData.last);
    } catch (error) {
      handleApiError('추가 레시피를 가져오는 중 오류가 발생했습니다.', error);
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * "돌아가기" 버튼 → 냉장고 화면으로 복귀
   */
  const resetAndGoBack = () => {
    setRecipes([]);
    setRecipePage(1);
    setHasMoreRecipes(true);

    localStorage.removeItem('recipes');
    localStorage.removeItem('recipePage');
    localStorage.removeItem('hasMoreRecipes');

    navigate('/refrigerator');
  };

  /**
   * IntersectionObserver 콜백
   */
  const handleObserver = ([entry]) => {
    if (entry.isIntersecting && hasMoreRecipes) {
      loadMoreRecipes();
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col items-center">
      <Header title="냉장고 파먹기" />

      {location.pathname === '/refrigerator' ? (
        // 냉장고 메인 화면
        <>
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

          {/* 레시피 찾기 버튼 (옅은 노란색/비활성 로직 제거) */}
          <div className="w-full px-4 mt-auto mb-4">
            <button
              onClick={handleFindRecipes}
              className="w-full py-3 rounded-lg text-lg font-medium bg-[var(--primary-color)] text-white"
            >
              레시피 찾기
            </button>
          </div>
        </>
      ) : (
        // /refrigerator/recipe 화면 (레시피 목록)
        <>
          <div className="w-full px-4 mt-6">
            <h2 className="text-lg font-medium mb-4">추천 레시피</h2>
            <div className="grid grid-cols-1 gap-6 min-h-[400px]">
              {recipes.length > 0 &&
                recipes.map((recipe) => (
                  <RecipeCard key={recipe.id} recipe={recipe} />
                ))}
            </div>
            {hasMoreRecipes && <div ref={observerRef} className="h-10"></div>}

            <button
              className="bg-[var(--primary-color)] text-white w-full py-3 mt-4 mb-4 rounded-lg text-lg font-medium"
              onClick={resetAndGoBack}
            >
              돌아가기
            </button>
            <TopButton
              offset={100}
              positionClass="bottom-20 right-[calc((100vw-375px)/2+16px)]"
            />
          </div>
        </>
      )}
    </div>
  );
}

export default Refrigerator;
