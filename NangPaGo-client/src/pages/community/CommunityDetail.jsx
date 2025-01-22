import { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axiosInstance from '../../api/axiosInstance';
import Community from '../../components/community/Community';

function CommunityDetail() {
  const { id } = useParams();
  const [community, setCommunity] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  const nickname = useSelector((state) => state.loginSlice.nickname);
  const isLoggedIn = Boolean(nickname);

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        const response = await axiosInstance.get(`/api/community/${id}`);
        console.log(response);
        setCommunity(response.data.data);
      } catch (err) {
        navigate('/community');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCommunity();
  }, [id]);

  useEffect(() => {
    const handlePopState = () => {
      const previousUrl = location.state?.from;

      if (previousUrl && (previousUrl.includes('/community/new') || previousUrl.includes(`/modify`))) {
        navigate('/community');
      }
    };

    window.addEventListener('popstate', handlePopState);

    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, [navigate, location.state]);
  
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

  return <Community community={community} isLoggedIn={isLoggedIn} />;
}

export default CommunityDetail;
