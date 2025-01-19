function CommentList({
  comments,
  isEditing,
  editedComment,
  onEditChange,
  onEditSubmit,
  onDeleteClick,
  onSetEditing,
  maskEmail,
  handleKeyDown,
}) {
  return (
    <div className="mt-4 space-y-3">
      {comments.map((comment) => (
        <div key={comment.id} className="border-b pb-2 flex flex-col space-y-2">
          <div>
            {isEditing === comment.id ? (
              <div className="grid grid-cols-2 gap-2">
                <textarea
                  value={editedComment}
                  onChange={(e) => onEditChange(e.target.value)}
                  onKeyDown={handleKeyDown}
                  className="col-span-2 w-full p-2 border border-gray-300 rounded-md"
                />
                <button
                  onClick={() => onEditSubmit(comment.id)}
                  className="text-white px-4 py-2"
                >
                  수정
                </button>
                <button
                  onClick={() => onSetEditing(null)}
                  className="text-white px-4 py-2"
                >
                  취소
                </button>
              </div>
            ) : (
              <>
                <p className="text-text-600 text-sm break-words">
                  <strong>{maskEmail(comment.email)}</strong>: {comment.content}
                </p>
                <p className="text-text-600 text-xs">
                  {new Date(comment.updatedAt).toLocaleString()}
                </p>
              </>
            )}
          </div>

          {comment.isOwnedByUser && isEditing !== comment.id && (
            <div className="flex gap-2">
              <button
                onClick={() => {
                  onSetEditing(comment.id);
                  onEditChange(comment.content);
                }}
                className="bg-primary px-4 py-2"
              >
                수정
              </button>
              <button
                onClick={() => onDeleteClick(comment.id)}
                className="bg-primary px-4 py-2"
              >
                삭제
              </button>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}

export default CommentList;
