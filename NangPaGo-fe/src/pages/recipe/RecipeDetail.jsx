import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { fetchRecipeById } from '../../api/recipe';
import Recipe from '../../components/recipe/Recipe';

function RecipeDetail() {
  const { id } = useParams();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const email = useSelector((state) => state.loginSlice.email);
  const isLoggedIn = Boolean(email);

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        const data = await fetchRecipeById(id);
        setRecipe(data);
      } catch (err) {
        setError('레시피를 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecipe();
  }, [id]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-primary"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-primary">{error}</p>
      </div>
    );
  }

  return <Recipe recipe={recipe} isLoggedIn={isLoggedIn} />;
}

export default RecipeDetail;
