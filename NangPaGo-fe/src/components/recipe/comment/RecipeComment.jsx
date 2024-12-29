import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
  fetchComments,
  createComment,
  deleteComment,
  updateComment,
} from '../../../api/commentService.js';
import LoginModal from '../../../common/modal/LoginModal';
import DeleteModal from '../../../common/modal/DeleteModal';

function RecipeComment({ recipeId }) {
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [isEditing, setIsEditing] = useState(null);
  const [editedComment, setEditedComment] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [commentToDelete, setCommentToDelete] = useState(null);

  const userEmail = useSelector((state) => state.loginSlice.email);
  const isLoggedIn = Boolean(userEmail);

  useEffect(() => {
    if (recipeId) {
      loadComments();
    }
  }, [recipeId]);

  const loadComments = async () => {
    setIsLoading(true);
    try {
      const response = await fetchComments(recipeId);
      setComments(response.data.data.reverse()); // 최신 댓글이 맨 위로 오도록 reverse() 사용
    } catch (error) {
      console.error('댓글을 불러오는 중 오류가 발생했습니다.', error);
      alert('댓글을 불러오는 중 문제가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCommentSubmit = async () => {
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
      const response = await createComment(recipeId, {
        email: userEmail,
        content: commentText,
      });
      const newComment = response.data.data;
      setComments((prevComments) => [newComment, ...prevComments]);
      setCommentText('');
    } catch (error) {
      console.error('댓글 생성 중 오류가 발생했습니다.', error);
      alert('댓글 생성 중 문제가 발생했습니다.');
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
      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.id === commentId
            ? { ...comment, content: editedComment }
            : comment,
        ),
      );
      setIsEditing(null);
      setEditedComment('');
    } catch (error) {
      console.error('댓글 수정 중 오류가 발생했습니다.', error);
      alert('댓글 수정 중 문제가 발생했습니다.');
    }
  };

  const handleDeleteComment = async () => {
    if (!commentToDelete) return;

    try {
      await deleteComment(recipeId, commentToDelete);
      setComments((prevComments) =>
        prevComments.filter((comment) => comment.id !== commentToDelete),
      );
      setCommentToDelete(null);
      setShowDeleteModal(false);
    } catch (error) {
      console.error('댓글 삭제 중 오류가 발생했습니다.', error);
      alert('댓글 삭제 중 문제가 발생했습니다.');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleCommentSubmit();
    }
  };

  return (
    <div className="mt-8">
      <h2 className="text-lg font-semibold mb-4">댓글</h2>
      <textarea
        value={commentText}
        onChange={(e) => setCommentText(e.target.value)}
        onKeyPress={handleKeyPress}
        className="w-full p-2 border border-gray-300 rounded-md mb-4"
        placeholder={
          isLoggedIn
            ? '댓글을 입력하세요...'
            : '로그인 후 댓글을 입력할 수 있습니다.'
        }
        disabled={!isLoggedIn || isSubmitting}
      />
      <button
        onClick={handleCommentSubmit}
        className="block bg-[var(--primary-color)] text-white px-4 py-2 rounded-md"
      >
        전송
      </button>

      {isLoading ? (
        <p className="text-center text-gray-500 mt-4">로딩 중...</p>
      ) : (
        <div className="mt-4 space-y-4">
          {comments.map((comment) => (
            <div key={comment.id} className="border-b pb-4">
              {isEditing === comment.id ? (
                <>
                  <textarea
                    value={editedComment}
                    onChange={(e) => setEditedComment(e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-md"
                  />
                  <button
                    onClick={() => handleEditComment(comment.id)}
                    className="mt-2 bg-blue-500 text-white px-4 py-2 rounded-md"
                  >
                    수정 완료
                  </button>
                  <button
                    onClick={() => setIsEditing(null)}
                    className="mt-2 ml-2 bg-gray-500 text-white px-4 py-2 rounded-md"
                  >
                    취소
                  </button>
                </>
              ) : (
                <>
                  <p className="text-gray-700 text-sm">
                    <strong>{comment.userEmail.slice(0, 3)}***</strong>:{' '}
                    {comment.content}
                  </p>
                  <p className="text-gray-500 text-xs">
                    {new Date(comment.createdAt).toLocaleString()}
                  </p>
                  {comment.userEmail === userEmail && (
                    <div className="flex gap-2 mt-2">
                      <button
                        onClick={() => {
                          setIsEditing(comment.id);
                          setEditedComment(comment.content);
                        }}
                        className="text-blue-500"
                      >
                        수정
                      </button>
                      <button
                        onClick={() => {
                          setCommentToDelete(comment.id);
                          setShowDeleteModal(true);
                        }}
                        className="text-red-500"
                      >
                        삭제
                      </button>
                    </div>
                  )}
                </>
              )}
            </div>
          ))}
        </div>
      )}
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
