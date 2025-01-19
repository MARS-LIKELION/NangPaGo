function UpdateUserInfoModal({ isOpen }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50 min-w-80 ">
      <div className="bg-white p-8 rounded-md relative flex flex-col items-center justify-center max-w-[400px]">
        <p className="text-center">회원 정보를 수정하였습니다.</p>
        <div className="mt-4 flex gap-4">
          <button
            onClick={() => (window.location.href = '/')}
            className="bg-primary text-white px-5 py-3"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
}

export default UpdateUserInfoModal;
