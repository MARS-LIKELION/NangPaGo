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

function ModifyCommunity() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    const fetchCommunity = async () => {
      try {
        const { data } = await getCommunityDetail(id);
        if (!data.isOwnedByUser) {
          alert('접근 권한이 없습니다.');
          navigate('/community');
          return;
        }
        setTitle(data.title);
        setContent(data.content);
        setIsPublic(data.isPublic);
        if (data.imageUrl) setImagePreview(data.imageUrl);
      } catch (err) {
        console.error('게시글 데이터를 가져오는 중 오류 발생:', err);
        setError('게시글 데이터를 불러오는 중 문제가 발생했습니다.');
      }
    };
    fetchCommunity();
  }, [id, navigate]);

  // Handle file preview
  useEffect(() => {
    if (file) {
      const objectUrl = URL.createObjectURL(file);
      setImagePreview(objectUrl);
      return () => URL.revokeObjectURL(objectUrl);
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
      const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content);
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
        />
        <TextArea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="내용을 입력해 주세요."
          rows={6}
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
        <SubmitButton onClick={handleSubmit} label="수정 완료" />
      </div>
      <Footer />
    </div>
  );
}

export default ModifyCommunity;
