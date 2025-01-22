import { useEffect, useState, useCallback } from 'react';
import { useSelector } from 'react-redux';
import {
  fetchComments,
  createComment,
  deleteComment,
  updateComment,
} from '../../../api/communityComment.js';
import LoginModal from '../../../common/modal/LoginModal';
import DeleteModal from '../../../common/modal/DeleteModal';
import {
  FaArrowLeft,
  FaArrowRight,
  FaAngleDoubleLeft,
  FaAngleDoubleRight,
  FaPen,
  FaTrash,
} from 'react-icons/fa';
import { BsThreeDots } from 'react-icons/bs';

function CommunityComment({ communityId }) {
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

  const isLoggedIn = useSelector((state) => Boolean(state.loginSlice.email));

  const [openDropdownId, setOpenDropdownId] = useState(null);

  // 댓글 로드 함수
  const loadComments = useCallback(
    async (page) => {
      setError(null);
      try {
        const response = await fetchComments(communityId, page, 5);
        const data = response.data.data;
        setComments(data.content);
        setCurrentPage(data.currentPage);
        setTotalPages(data.totalPages);
        setTotalItems(data.totalItems);
      } catch (error) {
        alert('댓글을 불러오는 중 문제가 발생했습니다.');
      }
    },
    [communityId],
  );

  useEffect(() => {
    if (communityId) {
      loadComments(0);
    }
  }, [communityId, loadComments]);

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
      await createComment(communityId, { content: commentText });
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
      await updateComment(communityId, commentId, { content: editedComment });
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
      await deleteComment(communityId, commentToDelete);
      await loadComments(currentPage);
      setCommentToDelete(null);
      setShowDeleteModal(false);
    } catch (error) {
      alert('댓글 삭제 중 문제가 발생했습니다.');
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      loadComments(newPage);
    }
  };

  const maskEmail = (email) => {
    const visiblePart = email.slice(0, 3);
    return `${visiblePart}***`;
  };

  const handleDropdownClick = (commentId, e) => {
    e.stopPropagation();
    setOpenDropdownId(openDropdownId === commentId ? null : commentId);
  };

  useEffect(() => {
    const handleClickOutside = () => setOpenDropdownId(null);
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <div className="mt-8 px-4">
      <div className="mt-10 flex justify-center items-center border-t-2 border-b-2 border-gray-300 p-4 mx-auto">
        <h1 className="text-18px font-bold md:text-30px">
          댓글 ({totalItems})
        </h1>
      </div>

      <div className="mt-4 space-y-3">
        {comments.map((comment) => (
          <div key={comment.id} className="border-b pb-2">
            <div className="flex justify-between items-start">
              <div className="flex flex-col w-full">
                <p className="text-text-600 text-sm break-words whitespace-pre-wrap">
                  <p className="opacity-70">{maskEmail(comment.email)}</p>
                  {isEditing === comment.id ? (
                    <div className="mt-2">
                      <textarea
                        value={editedComment}
                        onChange={(e) => setEditedComment(e.target.value)}
                        rows={editedComment.split('\n').length}
                        className="w-full p-2 border border-gray-300 rounded-md mb-2 resize-none"
                      />
                      <div className="flex gap-2 justify-end">
                        <button
                          onClick={() => setIsEditing(null)}
                          className="bg-gray-500 text-white px-4 py-2 rounded-md"
                        >
                          취소
                        </button>
                        <button
                          onClick={() => handleEditComment(comment.id)}
                          className="bg-primary text-white px-4 py-2 rounded-md"
                        >
                          등록
                        </button>
                      </div>
                    </div>
                  ) : (
                    <p className="text-base">{comment.content}</p>
                  )}
                </p>
                <p className="text-text-600 text-xs opacity-50">
                  {new Date(comment.updatedAt).toLocaleString()}
                </p>
              </div>

              {comment.isOwnedByUser && !isEditing && (
                <div className="relative">
                  <button
                    onClick={(e) => handleDropdownClick(comment.id, e)}
                    className="p-1 bg-transparent rounded-full"
                    aria-label="더보기"
                  >
                    <BsThreeDots className="w-5 h-5 text-gray-500" />
                  </button>
                  
                  {openDropdownId === comment.id && (
                    <div className="absolute right-0 mt-1 w-24 bg-white border border-gray-200 rounded-md shadow-lg z-10">
                      <button
                        onClick={() => {
                          setIsEditing(comment.id);
                          setEditedComment(comment.content);
                          setOpenDropdownId(null);
                        }}
                        className="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 flex items-center gap-2 bg-white text-gray-900"
                      >
                        <FaPen className="w-4 h-4 text-primary" />
                        수정
                      </button>
                      <button
                        onClick={() => {
                          setCommentToDelete(comment.id);
                          setShowDeleteModal(true);
                          setOpenDropdownId(null);
                        }}
                        className="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 flex items-center gap-2 bg-white text-gray-900"
                      >
                        <FaTrash className="w-4 h-4 text-red-500" />
                        삭제
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      <div className="flex justify-center items-center gap-2 mt-6">
        <button
          onClick={() => handlePageChange(0)}
          disabled={currentPage === 0}
          className={`px-1 py-2 bg-white ${
            currentPage === 0
              ? 'text-gray-300'
              : 'text-secondary'
          }`}
        >
          <FaAngleDoubleLeft size={20} />
        </button>
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className={`px-1 py-2 bg-white ${
            currentPage === 0
              ? 'text-gray-300'
              : 'text-secondary'
          }`}
        >
          <FaArrowLeft size={20} />
        </button>
        <span>
          {currentPage + 1} / {totalPages}
        </span>
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage + 1 >= totalPages}
          className={`px-1 py-2 bg-white ${
            currentPage + 1 >= totalPages
              ? 'text-gray-300'
              : 'text-secondary'
          }`}
        >
          <FaArrowRight size={20} />
        </button>
        <button
          onClick={() => handlePageChange(totalPages - 1)}
          disabled={currentPage + 1 >= totalPages}
          className={`px-1 py-2 bg-white ${
            currentPage + 1 >= totalPages
              ? 'text-gray-300'
              : 'text-secondary'
          }`}
        >
          <FaAngleDoubleRight size={20} />
        </button>
      </div>

      <form onSubmit={handleCommentSubmit} className="mt-4">
        <textarea
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
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
          className={`block w-full text-white mb-4 px-4 py-2 ${
            isSubmitting ? 'cursor-not-allowed' : ''
          }`}
          disabled={isSubmitting}
        >
          등록
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

export default CommunityComment;
