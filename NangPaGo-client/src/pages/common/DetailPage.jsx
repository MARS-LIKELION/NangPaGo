import { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from "../../hooks/useAuth";
import { fetchPostById } from '../../api/post';
import { fetchUserRecipeById } from '../../api/userRecipe';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import { PAGE_STYLES } from '../../common/styles/ListPage';
import Comment from '../../components/comment/Comment';
import Recipe from '../../components/recipe/Recipe';
import Community from '../../components/community/Community';
import UserRecipeDetail from '../../components/userRecipe/UserRecipeDetail';
// CreateButton aside 제거 (user-recipe 타입에 대해)
const pageComponents = {
  recipe: Recipe,
  community: Community,
  "user-recipe": UserRecipeDetail,
};

function DetailPage({ type }) {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isTopButtonVisible, setIsTopButtonVisible] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { isLoggedIn } = useAuth();

  const post = useMemo(() => {
    if (!id) throw new Error('ID is required');
    return { type, id };
  }, [type, id]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        let response;
        if (type === "user-recipe") {
          response = await fetchUserRecipeById(id);
          setData(response);
        } else {
          response = await fetchPostById({ type, id });
          const fetchedData = response.data.data ? response.data.data : response.data;
          setData(fetchedData);
        }
      } catch (err) {
        setError(
          `${type === 'recipe' ? '레시피' : type === 'user-recipe' ? '유저 레시피' : '게시물'}을 불러오는데 실패했습니다.`
        );
        setTimeout(() => {
          navigate(type === 'recipe' ? `/` : `/${type}`);
        }, 3000);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, type, navigate]);

  useEffect(() => {
    const handleScroll = () => setIsTopButtonVisible(window.scrollY > 100);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    const handlePopState = () => {
      const previousUrl = location.state?.from;
      if (previousUrl && (previousUrl.includes(`/new`) || previousUrl.includes(`/modify`))) {
        navigate(type === 'recipe' ? `/` : `/${type}`);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [navigate, location.state, type]);

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

  const Component = pageComponents[type];

  return (
    <div className={PAGE_STYLES.wrapper}>
      <Header />
      <main className={PAGE_STYLES.body}>
        {Component ? <Component post={post} data={data} isLoggedIn={isLoggedIn} /> : null}
        <Comment post={post} isLoggedIn={isLoggedIn} />
      </main>
      {/* user-recipe 타입에 대해서는 DetailPage에서 CreateButton aside를 제거 */}
      <Footer />
    </div>
  );
}

export default DetailPage;
