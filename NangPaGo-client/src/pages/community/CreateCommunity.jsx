import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { createCommunity } from '../../api/community';
import Header from '../../components/layout/header/Header';
import Footer from '../../components/layout/Footer';
import TextInput from '../../components/community/TextInput';
import TextArea from '../../components/community/TextArea';
import FileUpload from '../../components/community/FileUpload';
import { ERROR_STYLES } from '../../common/styles/ErrorMessage';
import SubmitButton from '../../components/button/SubmitButton';
import FileSizeErrorModal from '../../components/modal/FileSizeErrorModal';

const MAX_FILE_SIZE = 10 * 1024 * 1024;

function CreateCommunity() {
  const navigate = useNavigate();
  const location = useLocation();
  const prevPath = sessionStorage.getItem('prevPath') || '/community';

  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [file, setFile] = useState(null);
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [showFileSizeError, setShowFileSizeError] = useState(false);
  const [isBlocked, setIsBlocked] = useState(false);

  useEffect(() => {
    if (location.state?.from) {
      sessionStorage.setItem('prevPath', location.state.from);
    }
    return () => sessionStorage.removeItem('prevPath');
  }, [location.state?.from]);

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

  const handleFileChange = useCallback(
    (e) => {
      const selectedFile = e.target.files[0];
      if (selectedFile && selectedFile !== file) {
        setFile(selectedFile);
        setIsBlocked(true);
      }
    },
    [file],
  );

  useEffect(() => {
    const handleRefreshUnload = (e) => {
      if (isBlocked) {
        e.preventDefault();
        e.returnValue = '';
      }
    };

    const handleBackNavigation = (e) => {
      if (isBlocked) {const confirmed = window.confirm(
          '작성 중인 내용을 저장하지 않고 이동하시겠습니까?',
          );
        if (confirmed) {
          setIsBlocked(false);
          navigate(prevPath);
        } else {
          history.pushState(null, '', window.location.href);
        }
      }
    };

    window.addEventListener('beforeunload', handleRefreshUnload);
    window.addEventListener('popstate', handleBackNavigation);

    history.pushState(null, '', window.location.href);

    return () => {
      window.removeEventListener('beforeunload', handleRefreshUnload);
      window.removeEventListener('popstate', handleBackNavigation);
    };
  }, [isBlocked]);

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
      if (responseData.data?.id) {
        setIsBlocked(false);
        navigate(`/community/${responseData.data.id}`, {
          state: { from: '/community/new' },
        });
      } else {
        setError('게시글 등록 후 ID를 가져올 수 없습니다.');
      }
    } catch (err) {
      console.error('게시글 등록 중 오류 발생:', err);
      setError(err.message);
    }
  };

  return (
    <div className="bg-white shadow-md mx-auto min-w-80 min-h-screen flex flex-col max-w-screen-sm md:max-w-screen-md lg:max-w-screen-lg">
      <Header isBlocked={isBlocked} />
      <div className="flex-1 p-4">
        <TextInput
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            setIsBlocked(true);
          }}
          placeholder="제목"
        />
        <FileUpload
          file={file}
          onChange={handleFileChange}
          imagePreview={imagePreview}
          onCancel={() => {
            setFile(null);
            setImagePreview(null);
            setIsBlocked(true);
          }}
        />
        <TextArea
          value={content}
          onChange={(e) => {
            setContent(e.target.value);
            setIsBlocked(true);
          }}
          placeholder="내용을 입력해 주세요."
          rows={11}
        />
        <div className="flex items-center mb-4">
          <input
            type="checkbox"
            id="is-public"
            checked={!isPublic}
            onChange={(e) => {
              setIsPublic(!e.target.checked);
              setIsBlocked(true);
            }}
            className="mr-2 w-4 h-4 appearance-none border border-gray-400 rounded-md checked:bg-primary checked:focus:bg-primary checked:hover:bg-primary"
          />
          <label htmlFor="is-public" className="text-sm text-gray-500">
            비공개
          </label>
        </div>
        <p className={ERROR_STYLES.community}>{error}</p>
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