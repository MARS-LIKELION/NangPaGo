import { useEffect, useState, useCallback } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  fetchComments,
  createComment,
  deleteComment,
  updateComment,
} from '../../../api/commentService.js';
import LoginModal from '../../../components/modal/LoginModal';
import DeleteModal from '../../../components/modal/DeleteModal';
import CommentList from './CommentList';
import CommentForm from './CommentForm';
import Pagination from './Pagination';

function RecipeComment({ recipeId }) {
  const navigate = useNavigate();

  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [isEditing, setIsEditing] = useState(null);
  const [editedComment, setEditedComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [commentToDelete, setCommentToDelete] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);

  const isLoggedIn = useSelector((state) => Boolean(state.loginSlice.email));

  const loadComments = useCallback(
    async (page) => {
      try {
        const response = await fetchComments(recipeId, page, 5);
        const data = response.data.data;
        setComments(data.content);
        setCurrentPage(data.currentPage);
        setTotalPages(data.totalPages);
        setTotalItems(data.totalItems);
      } catch {
        alert('댓글을 불러오는 중 문제가 발생했습니다.');
      }
    },
    [recipeId],
  );

  useEffect(() => {
    if (recipeId) {
      loadComments(0);
    }
  }, [recipeId, loadComments]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }
    if (!commentText.trim()) {
      alert('댓글 내용을 입력하세요.');
      return;
    }

    setIsSubmitting(true);
    try {
      await createComment(recipeId, { content: commentText });
      await loadComments(currentPage);
      setCommentText('');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEditComment = async (commentId) => {
    if (!editedComment.trim()) {
      alert('수정할 댓글 내용을 입력하세요.');
      return;
    }
    try {
      await updateComment(recipeId, commentId, { content: editedComment });
      await loadComments(currentPage);
      setIsEditing(null);
      setEditedComment('');
    } catch {
      alert('댓글 수정 중 문제가 발생했습니다.');
    }
  };

  const handleDeleteComment = async () => {
    if (!commentToDelete) return;
    try {
      await deleteComment(recipeId, commentToDelete);
      await loadComments(currentPage);
      setShowDeleteModal(false);
    } catch {
      alert('댓글 삭제 중 문제가 발생했습니다.');
    }
  };

  const handleLoginRedirect = () => {
    setShowLoginModal(false);
    navigate('/login');
  };

  const maskEmail = (email) => `${email.slice(0, 3)}***`;

  return (
    <div className="mt-8 px-4">
      <div className="mt-10 flex justify-center items-center border-t-2 border-b-2 border-gray-300 p-4 mx-auto">
        <h1 className="text-18px font-bold md:text-30px">
          댓글 ({totalItems})
        </h1>
      </div>

      <CommentList
        comments={comments}
        isEditing={isEditing}
        editedComment={editedComment}
        onEditChange={setEditedComment}
        onEditSubmit={handleEditComment}
        onDeleteClick={(commentId) => {
          setCommentToDelete(commentId);
          setShowDeleteModal(true);
        }}
        onSetEditing={setIsEditing}
        maskEmail={maskEmail}
      />

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={loadComments}
      />

      <CommentForm
        commentText={commentText}
        isLoggedIn={isLoggedIn}
        isSubmitting={isSubmitting}
        onCommentChange={setCommentText}
        onSubmit={handleCommentSubmit}
      />
      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
      />
      <DeleteModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onDelete={handleDeleteComment}
      />
    </div>
  );
}

export default RecipeComment;
