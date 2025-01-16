import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCommunity } from '../../api/community';
import Header from '../../components/layout/header/Header.jsx';
import Footer from '../../components/common/Footer';
import TextInput from '../../components/community/TextInput';
import TextArea from '../../components/community/TextArea';
import FileUpload from '../../components/community/FileUpload';
import ErrorMessage from '../../components/common/ErrorMessage';
import SubmitButton from '../../components/common/SubmitButton';
import FileSizeErrorModal from '../../common/modal/FileSizeErrorModal';

const MAX_FILE_SIZE = 10 * 1024 * 1024;

function CreateCommunity() {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [showFileSizeError, setShowFileSizeError] = useState(false);

  useEffect(() => {
    if (file) {
      if (file.size > MAX_FILE_SIZE) {
        setShowFileSizeError(true);
      } else {
        const objectUrl = URL.createObjectURL(file);
        setImagePreview(objectUrl);

        return () => URL.revokeObjectURL(objectUrl);
      }
    }
  }, [file]);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile !== file) {
      setFile(selectedFile);
    }
  };

  const handleCancel = () => {
    setFile(null);
    setImagePreview(null);
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
      setError(err.message);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto w-[375px] min-h-screen flex flex-col">
      <Header />
      <div className="flex-1 p-4">
        <TextInput
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="제목"
        />
        <FileUpload
          file={file}
          onChange={handleFileChange}
          imagePreview={imagePreview}
          onCancel={handleCancel}
        />
        <TextArea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="내용을 입력해 주세요."
          rows={11}
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
            비공개
          </label>
        </div>
        <ErrorMessage error={error} />
        <SubmitButton onClick={handleSubmit} label="게시글 등록" />
      </div>
      <Footer />
      <FileSizeErrorModal
        isOpen={showFileSizeError}
        onClose={() => setShowFileSizeError(false)}
      />
    </div>
  );
}

export default CreateCommunity;
