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
import {
  FaArrowLeft,
  FaArrowRight,
  FaAngleDoubleLeft,
  FaAngleDoubleRight,
} from 'react-icons/fa';

function RecipeComment({ recipeId }) {
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [isEditing, setIsEditing] = useState(null);
  const [editedComment, setEditedComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [commentToDelete, setCommentToDelete] = useState(null);
  const [error, setError] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);

  const userEmail = useSelector((state) => state.loginSlice.email);
  const isLoggedIn = Boolean(userEmail);

  useEffect(() => {
    if (recipeId) {
      loadComments(0);
    }
  }, [recipeId]);

  const loadComments = async (page) => {
    setError(null);
    try {
      const response = await fetchComments(recipeId, page, 5);
      const data = response.data.data;
      setComments(data.content);
      setCurrentPage(data.currentPage);
      setTotalPages(data.totalPages);
      setTotalItems(data.totalItems);
    } catch (error) {
      alert('댓글을 불러오는 중 문제가 발생했습니다.');
    }
  };

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
      await createComment(recipeId, {
        email: userEmail,
        content: commentText,
      });
      await loadComments(currentPage);
      setCommentText('');
    } catch (error) {
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
      await loadComments(currentPage);
      setIsEditing(null);
      setEditedComment('');
    } catch (error) {
      alert('댓글 수정 중 문제가 발생했습니다.');
    }
  };

  const handleDeleteComment = async () => {
    if (!commentToDelete) return;

    try {
      await deleteComment(recipeId, commentToDelete);
      await loadComments(currentPage);
      setCommentToDelete(null);
      setShowDeleteModal(false);
    } catch (error) {
      alert('댓글 삭제 중 문제가 발생했습니다.');
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      if (isEditing !== null) {
        handleEditComment(isEditing);
      } else {
        handleCommentSubmit(e);
      }
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      loadComments(newPage);
      setCurrentPage(newPage);
    }
  };

  return (
    <div className="mt-8">
      <div className="mt-10 flex justify-center items-center border-t-2 border-b-2 border-gray-300 p-4 mx-auto">
        <h1 className="text-18px font-bold md:text-30px">
          댓글 ({totalItems})
        </h1>
      </div>

      <div className="mt-4 space-y-3">
        {Array.from({ length: 5 }).map((_, index) => {
          const comment = comments[index];
          return (
            <div key={comment ? comment.id : index} className="border-b pb-1">
              {comment ? (
                isEditing === comment.id ? (
                  <div className="grid grid-cols-2 gap-2">
                    <textarea
                      value={editedComment}
                      onChange={(e) => setEditedComment(e.target.value)}
                      onKeyDown={(e) => handleKeyDown(e)}
                      className="col-span-2 w-full p-2 border border-gray-300 rounded-md"
                    />
                    <button
                      onClick={() => handleEditComment(comment.id)}
                      className="bg-[var(--primary-color)] text-white px-4 py-2 rounded-md"
                    >
                      수정
                    </button>
                    <button
                      onClick={() => setIsEditing(null)}
                      className="bg-gray-500 text-white px-4 py-2 rounded-md"
                    >
                      취소
                    </button>
                  </div>
                ) : (
                  <div className="flex justify-between items-center">
                    <div>
                      <p className="text-gray-700 text-sm">
                        <strong>{comment.userEmail.slice(0, 3)}***</strong>:{' '}
                        {comment.content}
                      </p>
                      <p className="text-gray-500 text-xs">
                        {new Date(comment.createdAt).toLocaleString()}
                      </p>
                    </div>
                    {comment.userEmail === userEmail && (
                      <div className="flex gap-2">
                        <button
                          onClick={() => {
                            setIsEditing(comment.id);
                            setEditedComment(comment.content);
                          }}
                          className="text-[var(--primary-color)]"
                        >
                          수정
                        </button>
                        <button
                          onClick={() => {
                            setCommentToDelete(comment.id);
                            setShowDeleteModal(true);
                          }}
                          className="text-gray-500"
                        >
                          삭제
                        </button>
                      </div>
                    )}
                  </div>
                )
              ) : (
                <div className="h-8"></div> // Adjusted height to match when comments are present
              )}
            </div>
          );
        })}
      </div>

      <div className="flex justify-center items-center gap-2 mt-6">
        <button
          onClick={() => handlePageChange(0)}
          disabled={currentPage === 0}
          className={`px-1 py-2 rounded-md ${currentPage === 0 ? 'text-gray-300' : 'text-[var(--secondary-color)]'}`}
        >
          <FaAngleDoubleLeft size={20} />
        </button>
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className={`px-1 py-2 rounded-md ${currentPage === 0 ? 'text-gray-300' : 'text-[var(--secondary-color)]'}`}
        >
          <FaArrowLeft size={20} />
        </button>
        <span>
          {currentPage + 1} / {totalPages}
        </span>
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage + 1 >= totalPages}
          className={`px-1 py-2 rounded-md ${
            currentPage + 1 >= totalPages
              ? 'text-gray-300'
              : 'text-[var(--secondary-color)]'
          }`}
        >
          <FaArrowRight size={20} />
        </button>
        <button
          onClick={() => handlePageChange(totalPages - 1)}
          disabled={currentPage + 1 >= totalPages}
          className={`px-1 py-2 rounded-md ${
            currentPage + 1 >= totalPages
              ? 'text-gray-300'
              : 'text-[var(--secondary-color)]'
          }`}
        >
          <FaAngleDoubleRight size={20} />
        </button>
      </div>

      <form onSubmit={handleCommentSubmit} className="mt-4">
        <textarea
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
          onKeyDown={handleKeyDown}
          className="w-full p-2 border border-gray-300 rounded-md mb-4"
          placeholder={
            isLoggedIn
              ? '댓글을 입력하세요.'
              : '로그인 후 댓글을 입력할 수 있습니다.'
          }
          disabled={!isLoggedIn}
        />
        <button
          type="submit"
          className={`block w-full text-white px-4 py-2 rounded-md bg-[var(--primary-color)] ${
            isSubmitting ? 'cursor-not-allowed' : ''
          }`}
          disabled={isSubmitting}
        >
          전송
        </button>
      </form>

      {error && <p className="text-red-500 text-sm mt-4">{error}</p>}

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
