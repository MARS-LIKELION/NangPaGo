import { FaPen, FaTrash } from 'react-icons/fa';

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
  return (
    <div className="mt-4 space-y-3">
      {comments.map((comment) => (
        <div key={comment.id} className="border-b pb-2">
          {isEditing === comment.id ? (
            <div className="grid grid-cols-2 gap-2">
              <textarea
                value={editedComment}
                onChange={(e) => onEditChange(e.target.value)}
                className="col-span-2 w-full p-2 border border-gray-300 rounded-md"
              />
              <button
                onClick={() => onEditSubmit(comment.id)}
                className="bg-primary text-white px-4 py-2 rounded-md"
              >
                수정
              </button>
              <button
                onClick={() => onSetEditing(null)}
                className="bg-gray-500 text-white px-4 py-2 rounded-md"
              >
                취소
              </button>
            </div>
          ) : (
            <div className="flex justify-between items-start">
              <div className="flex flex-col">
                <p className="text-text-600 text-sm break-words">
                  <strong>{maskEmail(comment.email)}</strong>: {comment.content}
                </p>
                <p className="text-text-600 text-xs">
                  {new Date(comment.updatedAt).toLocaleString()}
                </p>
              </div>

              {comment.isOwnedByUser && (
                <div className="flex gap-2 ml-4">
                  <button
                    onClick={() => {
                      onSetEditing(comment.id);
                      onEditChange(comment.content);
                    }}
                    className="bg-transparent hover:opacity-70"
                    aria-label="댓글 수정"
                  >
                    <FaPen className="w-5 h-5 text-primary" />
                  </button>
                  <button
                    onClick={() => onDeleteClick(comment.id)}
                    className="bg-transparent hover:opacity-70"
                    aria-label="댓글 삭제"
                  >
                    <FaTrash className="w-5 h-5 text-red-500" />
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}

export default CommentList;
