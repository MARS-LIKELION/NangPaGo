import { FaPen, FaTrash } from 'react-icons/fa';
import { BsThreeDots } from 'react-icons/bs';
import { useState, useEffect } from 'react';

function CommentList({
  comments,
  isEditing,
  editedComment,
  onEditChange,
  onEditSubmit,
  onDeleteClick,
  onSetEditing,
  maskEmail,
}) {
  const [openDropdownId, setOpenDropdownId] = useState(null);

  const handleDropdownClick = (commentId, e) => {
    e.stopPropagation();
    setOpenDropdownId(openDropdownId === commentId ? null : commentId);
  };

  // 드롭다운 외부 클릭시 닫기
  useEffect(() => {
    const handleClickOutside = () => setOpenDropdownId(null);
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <div className="mt-4 space-y-3">
      {comments.map((comment) => (
        <div key={comment.id} className="border-b pb-2">
          <div className="flex justify-between items-start">
            <div className="flex flex-col w-full">
              <div className="text-text-600 text-sm break-words whitespace-pre-wrap">
                <p className="opacity-70">{comment.writerName}</p>
                {isEditing === comment.id ? (
                  <div className="mt-2">
                    <textarea
                      value={editedComment}
                      onChange={(e) => onEditChange(e.target.value)}
                      rows={editedComment.split('\n').length}
                      className="w-full p-2 border border-gray-300 rounded-md mb-2 resize-none"
                    />
                    <div className="flex gap-2 justify-end">
                      <button
                        onClick={() => onSetEditing(null)}
                        className="bg-gray-500 text-white px-4 py-2 rounded-md"
                      >
                        취소
                      </button>
                      <button
                        onClick={() => onEditSubmit(comment.id)}
                        className="bg-primary text-white px-4 py-2 rounded-md"
                      >
                        등록
                      </button>
                    </div>
                  </div>
                ) : (
                  <p className="text-base">{comment.content}</p>
                )}
              </div>
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
                        onSetEditing(comment.id);
                        onEditChange(comment.content);
                        setOpenDropdownId(null);
                      }}
                      className="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 flex items-center gap-2 bg-white text-gray-900"
                    >
                      <FaPen className="w-4 h-4 text-primary" />
                      수정
                    </button>
                    <button
                      onClick={() => {
                        onDeleteClick(comment.id);
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
  );
}

export default CommentList;
