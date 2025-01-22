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
          <div className="flex justify-between items-start">
            <div className="flex flex-col w-full">
              <p className="text-text-600 text-sm break-words whitespace-pre-wrap">
                <p className="opacity-70">{maskEmail(comment.email)}</p>
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
              </p>
              <p className="text-text-600 text-xs opacity-50">
                {new Date(comment.updatedAt).toLocaleString()}
              </p>
            </div>

            {comment.isOwnedByUser && !isEditing && (
              <div className="flex gap-2 ml-4 mt-4">
                <button
                  onClick={() => {
                    onSetEditing(comment.id);
                    onEditChange(comment.content);
                  }}
                  className="mr-2 bg-transparent hover:opacity-70"
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
        </div>
      ))}
    </div>
  );
}

export default CommentList;
