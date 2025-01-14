import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { updateCommunity, getCommunityDetail } from '../../api/community';
import Header from '../../components/common/Header';
import Footer from '../../components/common/Footer';
import TextInput from '../../components/community/TextInput';
import TextArea from '../../components/community/TextArea';
import FileUpload from '../../components/community/FileUpload';
import ErrorMessage from '../../components/common/ErrorMessage';
import SubmitButton from '../../components/common/SubmitButton';
import FileSizeErrorModal from '../../common/modal/FileSizeErrorModal';

const DEFAULT_IMAGE_URL = "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";
const MAX_FILE_SIZE = 10 * 1024 * 1024;

function ModifyCommunity() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [showFileSizeError, setShowFileSizeError] = useState(false);

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        const { data } = await getCommunityDetail(id);
        console.log(data);
        if (!data.isOwnedByUser) {
          navigate(`/community/${id}`, { replace: true });
          return;
        }
        setTitle(data.title);
        setContent(data.content);
        setIsPublic(data.isPublic);
        if (data.imageUrl && data.imageUrl !== DEFAULT_IMAGE_URL) {
          setImagePreview(data.imageUrl);
        } else {
          setImagePreview(null);
        }
      } catch (err) {
        console.error('게시글 데이터를 가져오는 중 오류 발생:', err);
        setError('게시글 데이터를 불러오는 중 문제가 발생했습니다.');
      }
    };
    fetchCommunity();
  }, [id, navigate]);

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
      const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content); // 줄바꿈 데이터
      console.log('전송할 content 데이터:', content); // 전송 전 content 확인
      formData.append('isPublic', isPublic);
      if (file) formData.append('file', file);
      await updateCommunity(id, formData);
      navigate(`/community/${id}`);
    } catch (err) {
      console.error('게시글 수정 중 오류 발생:', err);
      setError('게시글 수정 중 문제가 발생했습니다.');
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
            onChange={() => setIsPublic((prev) => !prev)}
            className="mr-2 w-4 h-4 appearance-none border border-gray-400 rounded-sm checked:bg-yellow-500 checked:border-yellow-500"
          />
          <label htmlFor="is-public" className="text-sm text-gray-500">
            비공개 (체크 시 로그인한 사용자만 볼 수 있습니다.)
          </label>
        </div>
        <ErrorMessage error={error} />
        <div className="mt-4">
          {' '}
          <SubmitButton onClick={handleSubmit} label="수정 완료" />
        </div>
      </div>
      <Footer className="mt-4" />
      <FileSizeErrorModal
        isOpen={showFileSizeError}
        onClose={() => setShowFileSizeError(false)}
      />
    </div>
  );
}

export default ModifyCommunity;
