import { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { fetchDetailById } from '../../api/detailPage';
import Recipe from '../../components/recipe/Recipe';
import Community from '../../components/community/Community';

const pageComponents = {
  recipe: Recipe,
  community: Community,
};

function DetailPage({ type }) {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  const email = useSelector((state) => state.loginSlice.email);
  const isLoggedIn = Boolean(email);

  const fetchData = async () => {
    try {
      const response = await fetchDetailById(type, id);
      setData(response);
    } catch (err) {
      setError(`${type === 'recipe' ? '레시피' : '게시물'}을 불러오는데 실패했습니다.`);
//       window.location.href = `/${type}`;
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id, type]);

  useEffect(() => {
    const handlePopState = () => {
      const previousUrl = location.state?.from;
      if (previousUrl && (previousUrl.includes(`/${type}/new`) || previousUrl.includes(`/modify`))) {
        navigate(`/${type}`);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
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

  return Component ? <Component data={data} isLoggedIn={isLoggedIn} /> : null;
}

export default DetailPage;
