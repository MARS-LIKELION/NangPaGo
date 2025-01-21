function CommentForm({
  commentText,
  isLoggedIn,
  isSubmitting,
  onCommentChange,
  onSubmit,
  handleKeyDown,
}) {
  const getPlaceholderText = () =>
    isLoggedIn ? '댓글을 입력하세요.' : '로그인 후 댓글을 입력할 수 있습니다.';

  const getButtonClassName = () =>
    `block w-full mb-4 px-4 py-2 bg-primary ${
      isSubmitting ? ' cursor-not-allowed' : ''
    }`;

  return (
    <form onSubmit={onSubmit} className="mt-4">
      <textarea
        value={commentText}
        onChange={(e) => onCommentChange(e.target.value)}
        onKeyDown={handleKeyDown}
        className="w-full p-2 border border-gray-300 rounded-md mb-4"
        placeholder={getPlaceholderText()}
        disabled={!isLoggedIn}
      />
      <button
        type="submit"
        className={getButtonClassName()}
        disabled={isSubmitting}
      >
        전송
      </button>
    </form>
  );
}

export default CommentForm;
