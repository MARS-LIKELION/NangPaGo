import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCommunity } from '../../api/community'; // createCommunity 가져오기
import Header from '../../components/common/Header';
import Footer from '../../components/common/Footer';

function CreateCommunity() {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    // 이미지 미리보기 URL 생성
    if (file) {
      const objectUrl = URL.createObjectURL(file);
      setImagePreview(objectUrl);

      // 컴포넌트 언마운트 시 URL 해제
      return () => URL.revokeObjectURL(objectUrl);
    } else {
      setImagePreview(null);
    }
  }, [file]);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async () => {
    if (!title || !content) {
      setError('제목과 내용을 모두 입력해주세요.');
      return;
    }

    try {
      const responseData = await createCommunity(
        { title, content, isPublic },
        file,
      );
      if (responseData.data && responseData.data.id) {
        navigate(`/community/${responseData.data.id}`);
      } else {
        setError('게시글 등록 후 ID를 가져올 수 없습니다.');
      }
    } catch (err) {
      console.error('게시글 등록 중 오류 발생:', err);
      setError('게시글 등록 중 문제가 발생했습니다.');
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />
      <div className="flex-1 p-4">
        <label>
          <input
            type="text"
            className="w-full p-2 border border-gray-300 rounded mb-4"
            placeholder="제목"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </label>
        <div className="flex flex-col items-center mb-4">
          <label
            htmlFor="file-upload"
            className="w-full h-40 border border-gray-300 rounded-md flex items-center justify-center bg-gray-100 cursor-pointer relative overflow-hidden"
          >
            {imagePreview ? (
              <img
                src={imagePreview}
                alt="Uploaded Preview"
                className="w-full h-full object-cover"
              />
            ) : (
              <span className="text-gray-400">사진 업로드</span>
            )}
            <input
              id="file-upload"
              type="file"
              accept="image/*"
              className="absolute inset-0 opacity-0 cursor-pointer"
              onChange={handleFileChange}
            />
          </label>
        </div>

        <textarea
          className="w-full p-2 border border-gray-300 rounded mb-4"
          placeholder="내용을 입력해 주세요."
          rows={6}
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />

        <div className="flex items-center mb-4">
          <input
            type="checkbox"
            id="is-public"
            checked={!isPublic}
            onChange={(e) => setIsPublic(!e.target.checked)}
            className="mr-2 w-4 h-4 appearance-none border border-gray-400 rounded-sm checked:bg-yellow-500 checked:border-yellow-500"
          />

          <label htmlFor="is-public" className="text-sm text-gray-500">
            비공개 (체크 시 로그인한 사용자만 볼 수 있습니다.)
          </label>
        </div>

        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

        <button
          onClick={handleSubmit}
          className="w-full bg-yellow-500 text-white py-2 rounded text-center font-semibold"
        >
          게시글 등록
        </button>
      </div>
      <Footer />
    </div>
  );
}

export default CreateCommunity;
