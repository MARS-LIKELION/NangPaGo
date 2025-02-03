import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import LoginModal from '../modal/LoginModal';
import DeleteModal from '../modal/DeleteModal';
import CommentList from './CommentList';
import CommentForm from './CommentForm';
import Pagination from './Pagination';
import {
  fetchComments,
  createComment,
  deleteComment,
  updateComment,
} from '../../api/comment';

function Comment({ post, isLoggedIn }) {
  const navigate = useNavigate();

  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [isEditing, setIsEditing] = useState(null);
  const [editedComment, setEditedComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [modalState, setModalState] = useState({
    type: null,
    data: null,
  });
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [defaultPage] = useState(0);

  const loadComments = useCallback(
    async (page) => {
      try {
        const response = await fetchComments(post, page + 1);
        const data = response.data.data;
        setComments(data.content);
        setCurrentPage(data.currentPage);
        setTotalPages(data.totalPages);
        setTotalItems(data.totalItems);
      } catch {
        alert('댓글을 불러오는 중 문제가 발생했습니다.');
      }
    },
    [post],
  );

  useEffect(() => {
    if (post) {
      loadComments(0);
    }
  }, [post, loadComments]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!isLoggedIn) {
      setModalState({
        type: 'login',
        data: '댓글 기능은 로그인 후 이용해 주세요.',
      });
      return;
    }
    if (!commentText.trim()) {
      alert('댓글 내용을 입력하세요.');
      return;
    }

    setIsSubmitting(true);
    try {
      await createComment(post, { content: commentText });
      await loadComments(defaultPage);
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
      await updateComment(post, commentId, { content: editedComment });
      await loadComments(currentPage);
      setIsEditing(null);
      setEditedComment('');
    } catch {
      alert('댓글 수정 중 문제가 발생했습니다.');
    }
  };

  const handleDeleteComment = async () => {
    if (!modalState.data) {
      return;
    }
    try {
      await deleteComment(post, modalState.data);
      await loadComments(currentPage);
      setModalState({ type: null });
    } catch {
      alert('댓글 삭제 중 문제가 발생했습니다.');
    }
  };

  const handleLoginRedirect = () => {
    setModalState({ type: null });
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
          setModalState({ type: 'delete', data: commentId });
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
      {modalState.type === 'login' && (
        <LoginModal
          isOpen={true}
          onClose={() => setModalState({ type: null })}
          description={modalState.data}
        />
      )}
      {modalState.type === 'delete' && (
        <DeleteModal
          isOpen={true}
          onClose={() => setModalState({ type: null })}
          onDelete={handleDeleteComment}
        />
      )}
    </div>
  );
}

export default Comment;
