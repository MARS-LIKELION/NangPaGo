import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux'; // Redux 상태 가져오기
import axiosInstance from '../../api/axiosInstance';
import Community from '../../components/community/Community';

function CommunityDetail() {
  const { id } = useParams();
  const [community, setCommunity] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const email = useSelector((state) => state.loginSlice.email);
  const isLoggedIn = Boolean(email);

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        const response = await axiosInstance.get(`/api/community/${id}`);
        setCommunity(response.data.data);
      } catch (err) {
        setError('게시물을 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchCommunity();
  }, [id]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-[var(--primary-color)]"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-50">
        <p className="text-[var(--primary-color)]">{error}</p>
      </div>
    );
  }

  return <Community community={community} isLoggedIn={isLoggedIn} />;
}

export default CommunityDetail;
