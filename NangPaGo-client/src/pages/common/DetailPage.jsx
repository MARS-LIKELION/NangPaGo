import React, { useEffect, useState, useMemo, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from "../../hooks/useAuth";
import { fetchPostById } from '../../api/post';
import Header from '../../components/layout/header/Header';
import ToggleButton from '../../components/button/ToggleButton';
import Footer from '../../components/layout/Footer';
import { PAGE_STYLES } from '../../common/styles/ListPage';
import { deleteCommunity } from '../../api/community';
import { deleteUserRecipe } from '../../api/userRecipe';
import Comment from '../../components/comment/Comment';
import Recipe from '../../components/recipe/Recipe';
import Community from '../../components/community/Community';
import UserRecipe from '../../components/userRecipe/UserRecipe';
import DeleteModal from '../../components/modal/DeleteModal';
import DeletePostSuccessModal from '../../components/modal/DeletePostSuccessModal';

const pageComponents = {
  recipe: Recipe,
  community: Community,
  "user-recipe": UserRecipe,
};

const deletePages = {
  community: deleteCommunity,
  "user-recipe": deleteUserRecipe,
};

function DetailPage({ type }) {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);

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
        const response = await fetchPostById(post);
        setData(response.data);
      } catch (err) {
        setError(
          `${type === 'recipe' ? '레시피' : type === 'user-recipe' ? '유저 레시피' : '커뮤니티'} 게시물을 불러오는데 실패했습니다.`
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
    const handlePopState = () => {
      const previousUrl = location.state?.from;
      if (previousUrl && (previousUrl.includes(`/create`) || previousUrl.includes(`/modify`))) {
        navigate(type === 'recipe' ? `/` : `/${type}`);
      }
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [navigate, location.state, type]);

  const handleCreateClick = useCallback(() => {
    navigate(`/${post.type}/create`,
      { state: { from: window.location.pathname } });
  }, [post.type, navigate]);

  const handleEditClick = useCallback(() => {
    navigate(`/${post.type}/${post.id}/modify`, {
      state: { from: window.location.pathname },
    });
  }, [post, navigate]);

  const handleDeleteClick = useCallback(() => {
    setIsDeleteModalOpen(true);
  }, []);

  const confirmDelete = useCallback(async () => {
      try {
        await deletePages[post.type](post.id);
        setIsDeleteModalOpen(false);
        setIsSuccessModalOpen(true);
      } catch (error) {
        console.error(error);
        alert(`${post.type === 'user-recipe' ? '유저 레시피' : '커뮤니티'} 게시물 삭제에 실패했습니다.`);
      }
    }, [post]);

  const actions = useMemo(() => {
    if (!data) return [];
    return data.isOwnedByUser
      ? [
          { label: '글작성', onClick: handleCreateClick },
          { label: '글수정', onClick: handleEditClick },
          { label: '글삭제', onClick: handleDeleteClick }
        ]
      : [{ label: '글작성', onClick: handleCreateClick }];
  }, [data, handleCreateClick, handleEditClick, handleDeleteClick]);

  if (!data) {
    return <LoadingSpinner />;
  }

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  const Component = pageComponents[type];

  return (
    <div className={PAGE_STYLES.wrapper}>
      <Header />
      <main className={PAGE_STYLES.body}>
        {Component && <Component post={post} data={data} isLoggedIn={isLoggedIn} />}
        <Comment post={post} isLoggedIn={isLoggedIn} />
      </main>
      {type !== "recipe" && <ToggleButton actions={actions} />}
      <Footer />
      <DeleteModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onDelete={confirmDelete}
      />
      <DeletePostSuccessModal
        isOpen={isSuccessModalOpen}
        onClose={() => setIsSuccessModalOpen(false)}
        type={post.type}
      />
    </div>
  );
}

export default DetailPage;

const LoadingSpinner = () => (
  <div className="flex justify-center items-center h-screen bg-gray-50">
    <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-primary"></div>
  </div>
);

const ErrorMessage = ({ message }) => (
  <div className="flex justify-center items-center h-screen bg-gray-50">
    <p className="text-primary">{message}</p>
  </div>
);
